package com.evashadidi.validator

import android.content.Context
import android.content.pm.PackageManager

object AppChecker {

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}