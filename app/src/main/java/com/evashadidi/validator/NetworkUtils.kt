package com.evashadidi.validator

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

object NetworkUtils {

    private val client = OkHttpClient()
    private const val API_ENDPOINT = "http://172.18.6.138:5000/"
    private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    fun sendPostRequest(message: String) {
        val json = """{ "event": "$message" }"""
        val body = RequestBody.create(MEDIA_TYPE_JSON, json)

        val request = Request.Builder()
            .url(API_ENDPOINT)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ValidatorApp", "HTTP Request Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("ValidatorApp", "HTTP Request Successful")
                response.close()
            }
        })
    }
}