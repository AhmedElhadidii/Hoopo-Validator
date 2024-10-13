// ApiRequestDao.kt
package com.evashadidi.validator

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApiRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apiRequest: ApiRequest)

    @Query("SELECT * FROM api_requests")
    suspend fun getAllRequests(): List<ApiRequest>

    @Query("DELETE FROM api_requests WHERE id = :id")
    suspend fun deleteById(id: Int)
}
