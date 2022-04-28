package com.github.displace.sdp2022.util.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.github.displace.sdp2022.map.MapViewManager

import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.tasks.CancellationTokenSource


import com.github.displace.sdp2022.util.math.CoordinatesUtil
import com.google.android.gms.common.util.Strings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint


/**
 * provides an API for GPS Position retrieval
 * @param activity context in which to retrieve the GPS position
 * @author LeoLgdr
 */
class GPSPositionManager(private val activity: Activity) {
    private var fusedLocationProviderClient: FusedLocationProviderClient
    val listenersManager = GeoPointListenersManager()
    private var isMocked = false
    private lateinit var mockLocation : GeoPoint

    /**
     * true iff the user enabled the required permissions for GPS
     */
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

    /**
     * update the gps position and call listeners depending on it
     */
    @SuppressLint("MissingPermission") // test is done in isGPSDisabled() but Lint does not detect it
    fun updateLocation() {
        if(isMocked) {
            listenersManager.invokeAll(mockLocation)
        } else {
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
    }

    /**
     * @param geoPoint mock geoPoint for location
     */
    @SuppressLint("MissingPermission")
    fun mockProvider(geoPoint: GeoPoint) {
        mockLocation = geoPoint
        isMocked = true
    }

    @SuppressLint("MissingPermission")
    fun unmockProvider() {
        isMocked = false
    }

    companion object {
        private const val REQUEST_CODE = 99
    }
}