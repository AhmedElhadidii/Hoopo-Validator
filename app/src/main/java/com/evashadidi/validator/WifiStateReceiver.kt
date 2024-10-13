// WifiStateReceiver.kt
package com.evashadidi.validator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class WifiStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    Log.d("ValidatorApp", "Wi-Fi is enabled")
                    NetworkUtils.sendPostRequest(context, "Wi-Fi enabled")
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    Log.d("ValidatorApp", "Wi-Fi is disabled")
                    NetworkUtils.sendPostRequest(context, "Wi-Fi disabled")
                }
            }
        }
    }
}
