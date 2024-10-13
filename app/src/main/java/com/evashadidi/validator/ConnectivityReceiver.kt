package com.evashadidi.validator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (networkCapabilities != null) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d("ValidatorApp", "Connected via Wi-Fi")
                    NetworkUtils.sendPostRequest(context, "Connected via Wi-Fi")
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.d("ValidatorApp", "Connected via Ethernet")
                    NetworkUtils.sendPostRequest(context, "Connected via Ethernet")
                }
                else -> {
                    Log.d("ValidatorApp", "Other Network Connected")
                    NetworkUtils.sendPostRequest(context, "Other Network Connected")
                }
            }
        } else {
            Log.d("ValidatorApp", "No Active Network")
            NetworkUtils.sendPostRequest(context, "No Active Network")
        }
    }

    private fun checkIfTetheringActive(connectivityManager: ConnectivityManager): Boolean {
        // This method requires reflection or hidden APIs, which are not recommended.
        // Alternatively, check for network interfaces that indicate tethering.
        val tetheredInterfaces = listOf("usb0", "rndis0", "eth1") // Common USB tethering interfaces
        val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
        for (iface in interfaces) {
            if (iface.name in tetheredInterfaces && iface.isUp) {
                return true
            }
        }
        return false
    }
}
