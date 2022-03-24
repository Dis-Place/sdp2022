package com.github.displace.sdp2022

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }


    @Suppress("UNUSED_PARAMETER")
    fun darkModeOnOff(view: View) {
    }

    @Suppress("UNUSED_PARAMETER")
    fun offlineModeOnOff(view: View) {
    }

    @Suppress("UNUSED_PARAMETER")
    fun musicOnOff(view: View) {
    }

    @Suppress("UNUSED_PARAMETER")
    fun sFXOnOff(view: View) {
    }
}