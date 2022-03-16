package com.github.blecoeur.bootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

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