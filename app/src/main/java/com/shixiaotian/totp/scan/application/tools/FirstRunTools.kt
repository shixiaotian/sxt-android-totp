package com.shixiaotian.totp.scan.application.tools

import android.content.Context
import android.content.SharedPreferences
import com.shixiaotian.totp.scan.application.common.MyConstants

class FirstRunTools {

    companion object {
        @JvmStatic fun isFirstRun(context: Context): Boolean {
            val prefs: SharedPreferences = context.getSharedPreferences(MyConstants.firstRunTag, Context.MODE_PRIVATE)
            val isFirstTime = prefs.getBoolean(MyConstants.firstRunTag + "isFirstTime", true)
            if (isFirstTime) {
                val editor = prefs.edit()
                editor.putBoolean(MyConstants.firstRunTag + "isFirstTime", false)
                editor.apply()
                return true
            }
            return false
        }

    }
}