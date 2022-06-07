package com.github.displace.sdp2022

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.util.ThemeManager

class PlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
    }
}