package com.evashadidi.validator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class ValidatorService : Service() {

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ValidatorServiceChannel"
        private const val APP_CHECK_INTERVAL = 60 * 1000L // 1 minute
    }

    private lateinit var simStateReceiver: SimStateReceiver
    private lateinit var wifiStateReceiver: WifiStateReceiver
    private lateinit var handler: Handler
    private lateinit var appCheckRunnable: Runnable

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Validator Service")
            .setContentText("Monitoring device events")
            .setSmallIcon(R.drawable.ic_validator)
            .build()
        startForeground(SERVICE_NOTIFICATION_ID, notification)

        initializeEventListeners()
    }

    private fun initializeEventListeners() {
        // Existing receivers
        simStateReceiver = SimStateReceiver()
        val simFilter = IntentFilter()
        simFilter.addAction("android.intent.action.SIM_STATE_CHANGED")
        registerReceiver(simStateReceiver, simFilter)

        wifiStateReceiver = WifiStateReceiver()
        val wifiFilter = IntentFilter()
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiStateReceiver, wifiFilter)

        // Register ConnectivityReceiver
        connectivityReceiver = ConnectivityReceiver()
        val connectivityFilter = IntentFilter()
        connectivityFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, connectivityFilter)

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service is already running
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
        unregisterReceiver(wifiStateReceiver)
        handler.removeCallbacks(appCheckRunnable)
        unregisterReceiver(connectivityReceiver)
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
            manager.createNotificationChannel(serviceChannel)
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
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(wifiStateReceiver, wifiFilter)

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
        val isInstalled = AppChecker.isAppInstalled(this, "com.hadidievas.hoopo")
        if (isInstalled) {
            NetworkUtils.sendPostRequest("Hoopo app is installed")
        } else {
            NetworkUtils.sendPostRequest("Hoopo app is not installed")
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this::class.java)
        restartServiceIntent.setPackage(packageName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restartServiceIntent)
        } else {
            startService(restartServiceIntent)
        }
        super.onTaskRemoved(rootIntent)
    }
}