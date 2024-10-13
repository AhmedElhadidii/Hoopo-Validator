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

object NetworkUtils {

    private val client = OkHttpClient()
    private const val API_ENDPOINT = "http://172.18.6.138:5000" // Replace with your actual endpoint
    private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    // Define the CHANNEL_ID constant
    private const val CHANNEL_ID = "ValidatorServiceChannel"

    /**
     * Sends an asynchronous POST request.
     * If the request fails, it caches the request for later retry.
     */
    fun sendPostRequest(context: Context, message: String) {
        val timestamp = System.currentTimeMillis()
        val json = "{ \"event\": \"$message\", \"timestamp\": $timestamp }"
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
                val json = "{ \"event\": \"$message\" }"
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
            .build()

        WorkManager.getInstance(ValidatorApp.context)
            .enqueueUniqueWork(
                "ApiRequestWorker",
                ExistingWorkPolicy.KEEP,
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
