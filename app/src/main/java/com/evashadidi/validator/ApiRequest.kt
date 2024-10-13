// ApiRequest.kt
package com.evashadidi.validator

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_requests")
data class ApiRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String
)
