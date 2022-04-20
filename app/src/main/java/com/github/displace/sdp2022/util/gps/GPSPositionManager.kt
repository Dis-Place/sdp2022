package com.github.displace.sdp2022.util.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.tasks.CancellationTokenSource


class GPSPositionManager(private val activity: Activity) {
    private var fusedLocationProviderClient: FusedLocationProviderClient
    val listenersManager = GeoPointListenersManager()

    fun isGPSDisabled(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

        if(isGPSDisabled()) {
            requestGPSPermissions()
        }

    }

    private fun isLocationEnabled(): Boolean {
        return (activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestGPSPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE,
        )
    }

    @SuppressLint("MissingPermission") // test is done in isGPSDisabled() but Lint does not detect it
    fun updateLocation() {
        if (isGPSDisabled()) {
            requestGPSPermissions()
            return
        }

        if(isLocationEnabled()) {
            fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnCompleteListener { task ->
                val location = task.result
                if(task.isSuccessful && location != null){
                    if(!activity.isDestroyed) {
                        listenersManager.invokeAll(CoordinatesUtil.geoPoint(location))
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 99
    }
}