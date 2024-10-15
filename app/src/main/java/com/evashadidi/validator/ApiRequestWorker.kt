// ApiRequestWorker.kt
package com.evashadidi.validator

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRequestWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val apiRequestDao = AppDatabase.getDatabase(appContext).apiRequestDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pendingRequests = apiRequestDao.getAllRequests()
            for (request in pendingRequests) {
                Log.d("ApiRequestWorker", "Processing request: ${request.message}")
                try {
                    val success = NetworkUtils.sendPostRequestSync(request.message)
                    if (success) {
                        apiRequestDao.deleteById(request.id)
                        Log.d("ApiRequestWorker", "Request successful, deleted from DB")
                    } else {
                        Log.d("ApiRequestWorker", "Request failed, will retry")
                    }
                } catch (e: Exception) {
                    Log.e("ApiRequestWorker", "Error processing request: ${e.message}")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ApiRequestWorker", "Error in doWork: ${e.message}")
            Result.retry()
        }
    }
}
