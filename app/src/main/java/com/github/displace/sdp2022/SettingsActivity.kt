package com.github.displace.sdp2022

import SettingsFragment
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

const val DARK_MODE_SETTINGS_SWITCH: String = "darkMode"
const val SFX_SETTINGS_SWITCH: String = "sfxKey"
const val MUSIC_SETTINGS_SWITCH: String = "musicKey"


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

            else -> "Unknown settings"
        }

        val stringToDisplay: String = contentString + enabledDisabledString

        //Display a toast message
        Toast.makeText(this, stringToDisplay, Toast.LENGTH_SHORT).show()
    }


}