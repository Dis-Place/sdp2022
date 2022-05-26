package com.github.displace.sdp2022.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.R

/**
 * to manage the theme (style) at the context (activity) level
 * @author LeoLgdr Blecoeur
 */
object ThemeManager {

    /**
     * set theme style according to the saved preferences
     * @param context
     */
    fun applyChosenTheme(context: Context) {
        when (PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.theme),"purple")) {
            "purple" -> context.theme.applyStyle(R.style.Theme_DisPlace1, true)
            "green" -> context.theme.applyStyle(R.style.Theme_DisPlace2, true)
            else -> context.theme.applyStyle(R.style.Theme_DisPlace1, true)
        }
    }
}