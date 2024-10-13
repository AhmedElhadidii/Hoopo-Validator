// SimStateReceiver.kt
package com.evashadidi.validator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SimStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.SIM_STATE_CHANGED") {
            Log.d("ValidatorApp", "SIM state changed")
            NetworkUtils.sendPostRequest("SIM state changed")
        }
    }
}
