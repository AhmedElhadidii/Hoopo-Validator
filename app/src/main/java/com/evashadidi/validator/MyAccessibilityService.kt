// MyAccessibilityService.kt
package com.evashadidi.validator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.evashadidi.validator.ValidatorApp.Companion.context

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: ""
            val className = event.className?.toString() ?: ""

            if (packageName == "com.android.settings") {
                Log.d("ValidatorApp", "Settings app opened")
                NetworkUtils.sendPostRequest(context,"Device settings opened")
            }
        }
    }

    override fun onInterrupt() {
        // Handle interrupt if necessary
    }
}
