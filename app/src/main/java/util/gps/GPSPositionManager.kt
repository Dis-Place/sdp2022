package util.gps

import android.app.Activity
import android.location.Location
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

class GPSPositionManager(activity: Activity) {
    private var locationProvider: GpsMyLocationProvider
    private lateinit var lastLocation: Location

    init {
        locationProvider = GpsMyLocationProvider(activity)
        locationProvider.locationUpdateMinDistance = MIN_UPDATE_DISTANCE
        locationProvider.locationUpdateMinTime = MIN_UPDATE_TIME
        locationProvider.onLocationChanged(lastLocation)
    }

    fun getPosition() : GeoPoint? {
        if(this::lastLocation.isInitialized) return CoordinatesConversionUtil.ofLocation(lastLocation)
        return null
    }

    companion object {
        val MIN_UPDATE_DISTANCE = 1.0F
        val MIN_UPDATE_TIME = 10.toLong()
    }
}