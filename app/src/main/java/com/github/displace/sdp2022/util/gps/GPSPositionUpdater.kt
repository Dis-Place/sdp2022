package com.github.displace.sdp2022.util.gps

import android.app.Activity
import kotlinx.coroutines.channels.ticker
import org.osmdroid.views.MapView
import java.util.*


class GPSPositionUpdater(private val activity: Activity, private val gpsPositionManager: GPSPositionManager) {

    lateinit var timer : Timer
    val timerTask = object : TimerTask() {
        override fun run() {
            if(!activity.isDestroyed) {
                gpsPositionManager.updateLocation()
            }
        }
    }

    init{
        initTimer()
    }

    private fun initTimer() {
        timer = Timer()
        timer.schedule(timerTask, SCHEDULE_DELAY_MILLIS, UPDATE_PERIOD_MILLIS)
    }

    fun stopUpdates(){
        timer.cancel()
    }

    companion object {
        const val UPDATE_PERIOD_MILLIS = 5000.toLong()
        const val SCHEDULE_DELAY_MILLIS = 5000.toLong()
    }
}