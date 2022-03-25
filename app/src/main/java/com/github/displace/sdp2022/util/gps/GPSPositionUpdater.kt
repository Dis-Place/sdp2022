package com.github.displace.sdp2022.util.gps

import android.app.Activity
import kotlinx.coroutines.channels.ticker
import org.osmdroid.views.MapView
import java.util.*


class GPSPositionUpdater(private val activity: Activity, private val gpsPositionManager: GPSPositionManager) {
    val listenersManager = GeoPointListenersManager()
    lateinit var timer : Timer
    val timerTask = object : TimerTask() {
        override fun run() {
            if(!activity.isDestroyed) {
                activity.run {
                    listenersManager.invokeAll(gpsPositionManager.getPosition())
                }
            }
        }
    }

    init{
        initTimer()
    }

    private fun initTimer() {
        timer = Timer()
        val timerTask =
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