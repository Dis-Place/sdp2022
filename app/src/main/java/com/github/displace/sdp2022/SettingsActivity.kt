package com.github.displace.sdp2022

import SettingsFragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.util.ThemeManager

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // needs to be called before super.onCreate
        ThemeManager.applyChosenTheme(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment())
            .commit()

        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPrefs.registerOnSharedPreferenceChangeListener { prefs, key ->
            handleChanges(prefs, key)
        }
    }

    private fun handleChanges(prefs: SharedPreferences, key: String) {
        /* if the theme has been changed, we need to restart the activity
        to apply the new theme */
        if(key == getString(R.string.theme)) {
            startActivity(this.intent)
        }
    }


    override fun onResume() {
        super.onResume()

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
    }

}