// NetworkUtils.kt

package com.evashadidi.validator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.work.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.telephony.TelephonyManager
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import java.net.NetworkInterface
import java.util.*

object NetworkUtils {

    private val client = OkHttpClient()
    private const val API_ENDPOINT = "http://172.18.6.138:5000/api" // Replace with your actual endpoint
    private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    // Define the CHANNEL_ID constant
    private const val CHANNEL_ID = "ValidatorServiceChannel"

    fun getIMEI(context: Context): String? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyManager.imei
        }
        return null
    }

    fun getIPAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in Collections.list(interfaces)) {
                val addrs = intf.inetAddresses
                for (addr in Collections.list(addrs)) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        // Check if it's an IPv4 address
                        if (sAddr.indexOf(':') < 0) return sAddr
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("NetworkUtils", "Error getting IP address: ${ex.message}")
        }
        return null
    }

    /**
     * Sends an asynchronous POST request.
     * If the request fails, it caches the request for later retry.
     */
    fun sendPostRequest(context: Context, message: String) {
        val timestamp = System.currentTimeMillis()
        val imei = getIMEI(context) ?: "unknown"
        val ip = getIPAddress() ?: "unknown"
        val json = """
            {
                "event": "$message",
                "timestamp": $timestamp,
                "imei": "$imei",
                "ip": "$ip"
            }
        """.trimIndent()
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
    
        val request = Request.Builder()
            .url(API_ENDPOINT)
            .post(body)
            .build()
    
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ValidatorApp", "HTTP Request Failed: ${e.message}")
                ValidatorApp.applicationScope.launch {
                    insertApiRequest(message, timestamp)
                }
            }
    
            override fun onResponse(call: Call, response: Response) {
                Log.d("ValidatorApp", "HTTP Request Successful")
                response.close()
            }
        })
    }

    /**
     * Sends a synchronous POST request.
     * Used by the WorkManager to retry cached requests.
     */
    suspend fun sendPostRequestSync(message: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val imei = getIMEI(ValidatorApp.context) ?: "unknown"
                val ip = getIPAddress() ?: "unknown"
                val json = """
                    {
                        "event": "$message",
                        "imei": "$imei",
                        "ip": "$ip"
                    }
                """.trimIndent()
                val body = json.toRequestBody(MEDIA_TYPE_JSON)

                val request = Request.Builder()
                    .url(API_ENDPOINT)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val success = response.isSuccessful
                response.close()
                success
            } catch (e: Exception) {
                Log.e("ValidatorApp", "HTTP Request Failed: ${e.message}")
                false
            }
        }
    }

    /**
     * Inserts a failed API request into the Room database and enqueues a worker to retry.
     */
    private suspend fun insertApiRequest(message: String, timestamp: Long) {
        val db = AppDatabase.getDatabase(ValidatorApp.context)
        val apiRequestDao = db.apiRequestDao()
        val apiRequest = ApiRequest(message = message, timestamp = timestamp)
        apiRequestDao.insert(apiRequest)

        enqueueApiRequestWorker()
    }

    /**
     * Enqueues a OneTimeWorkRequest to process cached API requests.
     */
    private fun enqueueApiRequestWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val apiRequestWork = OneTimeWorkRequestBuilder<ApiRequestWorker>()
            .setConstraints(constraints)
            .setInitialDelay(2, TimeUnit.MINUTES) // Retry every minute
            .build()

        WorkManager.getInstance(ValidatorApp.context)
            .enqueueUniqueWork(
                "ApiRequestWorker",
                ExistingWorkPolicy.REPLACE, // Replace to ensure it runs again
                apiRequestWork
            )
    }

    /**
     * Creates a notification channel for the service.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Validator Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

}
