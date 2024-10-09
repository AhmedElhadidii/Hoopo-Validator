package com.evashadidi.validator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class WifiStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    Log.d("ValidatorApp", "Wi-Fi Enabled")
                    NetworkUtils.sendPostRequest("Wi-Fi Enabled")
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    Log.d("ValidatorApp", "Wi-Fi Disabled")
                    NetworkUtils.sendPostRequest("Wi-Fi Disabled")
                }
                WifiManager.WIFI_STATE_ENABLING -> {
                    Log.d("ValidatorApp", "Wi-Fi Enabling")
                }
                WifiManager.WIFI_STATE_DISABLING -> {
                    Log.d("ValidatorApp", "Wi-Fi Disabling")
                }
            }
        }
    }
}