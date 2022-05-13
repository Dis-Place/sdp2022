package com.github.displace.sdp2022.sound

import android.R
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.SoundPool
import android.view.SoundEffectConstants


class SoundsManager(private val context: Context) {

    companion object {
        private const val MAX_STREAMS = 10
    }

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .build()

    fun clickSound() {
        val s = soundPool.load(context,com.github.displace.sdp2022.R.raw.sound_button_click,1)
    }



}