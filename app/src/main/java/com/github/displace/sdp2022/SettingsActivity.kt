package com.github.displace.sdp2022

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import displace.sdp2022.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun darkModeOnOff(view: View) {}
    fun offlineModeOnOff(view: View) {}
    fun musicOnOff(view: View) {}
    fun sFXOnOff(view: View) {}
}