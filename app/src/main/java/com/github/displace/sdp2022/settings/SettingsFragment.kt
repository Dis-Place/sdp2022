package com.github.displace.sdp2022.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.displace.sdp2022.R

/**
 * Settings fragment
 *
 * Just a very simple settings fragment, that will be used to display the settings.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


}