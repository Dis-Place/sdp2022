package com.github.displace.sdp2022.util.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.util.GeoPoint

class GPSPositionManager(private val activity: Activity) {
    private var currentLocation: GeoPoint? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient

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
        if (isGPSDisabled()) {
            requestGPSPermissions()
        } else if(isLocationProviderEnabled()){
            updateCurrentLocation()
        }
    }

    fun getPosition(): GeoPoint? {
        updateCurrentLocation()
        return currentLocation
    }

    private fun requestGPSPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    fun isLocationProviderEnabled(): Boolean {
        return (activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    }

    @SuppressLint("MissingPermission") // test is done in isGPSDisabled() but Lint does not detect it
    fun updateCurrentLocation() {
        if (isLocationProviderEnabled()) {
            if (isGPSDisabled()) {
                requestGPSPermissions()
                return
            }
            fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    currentLocation = CoordinatesConversionUtil.geoPoint(location)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 99
    }
}