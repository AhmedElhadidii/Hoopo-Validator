// ApiRequestWorker.kt
package com.evashadidi.validator

import android.content.Context
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
                try {
                    val success = NetworkUtils.sendPostRequestSync(request.message)
                    if (success) {
                        apiRequestDao.deleteById(request.id)
                    }
                } catch (e: Exception) {
                    // Log the failure and proceed to the next request
                    e.printStackTrace()
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
