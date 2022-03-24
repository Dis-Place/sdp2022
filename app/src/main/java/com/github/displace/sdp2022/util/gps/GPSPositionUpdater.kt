package com.github.displace.sdp2022.util.gps

import android.app.Activity
import kotlinx.coroutines.channels.ticker
import java.util.*


class GPSPositionUpdater(private val activity: Activity, private val gpsPositionManager: GPSPositionManager) {
    val listenersManager = GeoPointListenersManager()
    lateinit var timer : Timer

    init{
        initTimer()
    }

    private fun initTimer() {
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    listenersManager.invokeAll(gpsPositionManager.getPosition())
                }
            }
        }
        timer.schedule(timerTask, 0, UPDATE_PERIOD_MILLIS)
    }

    fun stopUpdates(){
        timer.cancel()
    }

    companion object {
        const val UPDATE_PERIOD_MILLIS = 5000.toLong()
    }
}