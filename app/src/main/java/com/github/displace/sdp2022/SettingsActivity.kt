package com.github.displace.sdp2022

import SettingsFragment
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.preference.PreferenceManager

const val DARK_MODE_SETTINGS_SWITCH: String = "darkMode"
const val SFX_SETTINGS_SWITCH: String = "sfxKey"
const val MUSIC_SETTINGS_SWITCH: String = "musicKey"
const val THEME_SETTINGS_SWITCH: String = "themeKey"


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment())
            .commit()

        val prefListener =
            OnSharedPreferenceChangeListener { prefs, key ->
                if (prefs != null && key != null)
                    handleChanges(prefs, key)
                else
                    Log.e("Null parameters", "the parameters are null")
            }
        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    private fun handleChanges(prefs: SharedPreferences, key: String) {
        //Retrieve the content of the setting
        val enabledDisabledString: String =
            if (prefs.getBoolean(key, false)) " enabled" else " disabled"

        //Retrieve which settings it is
        val contentString: String = when (key) {
            DARK_MODE_SETTINGS_SWITCH -> "Dark mode"
            SFX_SETTINGS_SWITCH -> "Sound effects"
            MUSIC_SETTINGS_SWITCH -> "Music"
            THEME_SETTINGS_SWITCH -> "Theme"

            else -> "Unknown settings"
        }

        when (key) {
            DARK_MODE_SETTINGS_SWITCH -> darkMode(prefs)
            SFX_SETTINGS_SWITCH -> sfx(prefs)
            MUSIC_SETTINGS_SWITCH -> music(prefs)
            THEME_SETTINGS_SWITCH -> chooseTheme(prefs)

        }
        val stringToDisplay: String = contentString + enabledDisabledString

        //Display a toast message
        Toast.makeText(this, stringToDisplay, Toast.LENGTH_SHORT).show()
    }

    private fun music(prefs: SharedPreferences) {
        //TODO: Implement later
    }

    private fun sfx(prefs: SharedPreferences) {
        //TODO: Implement later
    }

    private fun chooseTheme(prefs: SharedPreferences) {
        when (prefs.getString("themeKey", "purple")!!) {
            "purple" -> theme.applyStyle(R.style.Theme_DisPlace1, true)
            "green" -> theme.applyStyle(R.style.Theme_DisPlace2, true)
            else -> theme.applyStyle(R.style.Theme_DisPlace1, true)
        }

    }

    private fun darkMode(prefs: SharedPreferences) {
        val cur = prefs.getBoolean("darkMode", false)
        if (cur) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }


}