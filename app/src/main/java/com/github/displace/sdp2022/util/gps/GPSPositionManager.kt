package com.github.displace.sdp2022.util.gps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint

class GPSPositionManager(private val activity: Activity) {
    private var lastLocation: GeoPoint? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestGPSPermissions()
        }
    }

    fun getPosition(): GeoPoint? {
        initLastLocation()
        return lastLocation
    }

    private fun requestGPSPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    fun isLocationProviderEnabled(): Boolean {
        return (activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    }

    fun initLastLocation() {
        if (isLocationProviderEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestGPSPermissions()
                return
            }
            fusedLocationProviderClient.lastLocation.addOnCompleteListener() { task ->
                val location = task.result
                if (location != null) {
                    lastLocation = CoordinatesUtil.geoPoint(location)
                }
            }
        }
    }

    companion object {
        private val REQUEST_CODE = 99
    }
}