package com.github.displace.sdp2022.util

import android.app.Activity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration

/**
 * Utility class for initing preferences.
 *
 * @author LeoLgdr
 */
object PreferencesUtil {
    /**
     * Init osmdroid pref
     *
     * @param activity: Activity to get the context from
     */
    fun initOsmdroidPref(activity: Activity) {
        // Load the default preferences
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        //Set the user agent
        Configuration.getInstance().userAgentValue = activity.packageName
    }
}