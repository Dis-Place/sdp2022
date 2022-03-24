package com.github.displace.sdp2022.util

import android.app.Activity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration

object PreferencesUtil {
    fun initOsmdroidPref(activity: Activity) {
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        Configuration.getInstance().userAgentValue = activity.packageName
    }
}