package com.github.displace.sdp2022.util.gps

import android.content.Intent
import org.osmdroid.util.GeoPoint

/**
 * util for gps location mocking
 */
object MockGPS {

    const val MOCK_LAT_ID = "MOCK_LOCATION_LAT"
    const val MOCK_LON_ID = "MOCK_LOCATION_LON"



    /**
     * mock gps if specified by intent
     * @param intent
     * @param gpsPositionManager
     */
    fun mockIfNeeded(intent: Intent, gpsPositionManager: GPSPositionManager) {
        if(intent.hasExtra(MOCK_LAT_ID) && intent.hasExtra(MOCK_LON_ID)) {
            gpsPositionManager.mockProvider(GeoPoint(intent.getDoubleExtra(MOCK_LAT_ID,0.0),intent.getDoubleExtra(MOCK_LON_ID,0.0)))
        }
    }

    /**
     * specify an intent needs mock gps location
     * @param intent
     * @param mockLocation
     */
    fun specifyMock(intent: Intent, mockLocation: GeoPoint) {
        intent.putExtra(MOCK_LAT_ID,mockLocation.latitude)
        intent.putExtra(MOCK_LON_ID,mockLocation.longitude)

    }
}