// UsbDeviceReceiver.kt
package com.evashadidi.validator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.util.Log

class UsbDeviceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                Log.d("ValidatorApp", "USB device connected")
                NetworkUtils.sendPostRequest("USB device connected")
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                Log.d("ValidatorApp", "USB device disconnected")
                NetworkUtils.sendPostRequest("USB device disconnected")
            }
        }
    }
}
