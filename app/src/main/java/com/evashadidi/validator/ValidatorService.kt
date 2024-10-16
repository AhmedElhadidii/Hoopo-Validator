// ValidatorService.kt
package com.evashadidi.validator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import android.app.Application
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import android.net.ConnectivityManager
import android.app.PendingIntent
import android.app.AlarmManager

import java.util.concurrent.TimeUnit
//old import android.net.TetheringManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class ValidatorApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: ValidatorApp? = null

        val context: Context
            get() = instance!!.applicationContext

        val applicationScope = CoroutineScope(SupervisorJob())
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager to process cached API requests
        scheduleApiRequestWorker()
    }

    private fun scheduleApiRequestWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val apiRequestWork = PeriodicWorkRequestBuilder<ApiRequestWorker>(
            1, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        Log.d("APICachedRequests", "scheduleApiRequestWorker: trying to resend to the API")

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "ApiRequestWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                apiRequestWork
            )
    }
}



class ValidatorService : Service() {

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ValidatorServiceChannel"
        private const val APP_CHECK_INTERVAL = 2 * 60 * 1000L // 2 minutes
    }

    private lateinit var simStateReceiver: SimStateReceiver
    private lateinit var wifiStateReceiver: WifiStateReceiver
    private lateinit var usbDeviceReceiver: UsbDeviceReceiver
    private lateinit var handler: Handler
    private lateinit var appCheckRunnable: Runnable
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val processedEvents = mutableSetOf<String>() // To track processed events

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Validator Service")
            .setContentText("Monitoring device events")
            .setSmallIcon(R.drawable.ic_validator_logo_foreground) // Ensure you have this icon
            .setPriority(NotificationCompat.PRIORITY_LOW) // Set priority
            .build()
        startForeground(SERVICE_NOTIFICATION_ID, notification)

        Log.d("ValidatorService", "Service started and notification displayed")
        initializeEventListeners()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service is already running
        Log.d("ValidatorService", "Service onStartCommand called")

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ValidatorService", "Service destroyed")

        unregisterReceiver(simStateReceiver)
        unregisterReceiver(wifiStateReceiver)
        unregisterReceiver(usbDeviceReceiver)
        handler.removeCallbacks(appCheckRunnable)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We don't need binding
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Validator Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun initializeEventListeners() {
        // Initialize SIM state receiver
        simStateReceiver = SimStateReceiver()
        val simFilter = IntentFilter()
        simFilter.addAction("android.intent.action.SIM_STATE_CHANGED")
        registerReceiver(simStateReceiver, simFilter)

        // Initialize Wi-Fi state receiver
        wifiStateReceiver = WifiStateReceiver()
        val wifiFilter = IntentFilter()
        wifiFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        registerReceiver(wifiStateReceiver, wifiFilter)

        // Initialize USB device receiver
        usbDeviceReceiver = UsbDeviceReceiver()
        val usbFilter = IntentFilter()
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED")
        registerReceiver(usbDeviceReceiver, usbFilter)

        // Initialize ConnectivityManager for tethering and hotspot monitoring
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                // Check if the network has tethering capabilities
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        Log.d("ValidatorApp", "Wi-Fi connected (possibly hotspot)")
                        NetworkUtils.sendPostRequest(this@ValidatorService, "Wi-Fi connected (possibly hotspot)")
                    } else {
                        Log.d("ValidatorApp", "Wi-Fi disconnected (possibly hotspot turned off)")
                        NetworkUtils.sendPostRequest(this@ValidatorService, "Wi-Fi disconnected (possibly hotspot turned off)")
                    }
                }
                // Add more checks if needed
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("ValidatorApp", "Network lost")
                NetworkUtils.sendPostRequest(this@ValidatorService, "Network lost")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("ValidatorApp", "Network available")
                NetworkUtils.sendPostRequest(this@ValidatorService, "Network available")
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Initialize periodic app check
        handler = Handler(Looper.getMainLooper())
        appCheckRunnable = object : Runnable {
            override fun run() {
                checkAppInstallation()
                handler.postDelayed(this, APP_CHECK_INTERVAL)
            }
        }
        handler.post(appCheckRunnable)
    }

    private fun checkAppInstallation() {
        val packageName = "im.evas.app" // Ensure this is the correct package name
        Log.d("ValidatorServiceAppChecks", "Checking installation for package: $packageName")
        
        val isInstalled = AppChecker.isAppInstalled(this, packageName)
        val eventMessage = if (isInstalled) "Hoopo app is installed" else "Hoopo app is not installed"

        // Check if the event has already been processed
        if (!processedEvents.contains(eventMessage)) {
            NetworkUtils.sendPostRequest(this, eventMessage)
            processedEvents.add(eventMessage) // Mark this event as processed
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this::class.java)
        restartServiceIntent.setPackage(packageName)
        val pendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent
        )
        super.onTaskRemoved(rootIntent)
    }
}
