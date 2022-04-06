package com.github.displace.sdp2022.util.gps

import android.annotation.TargetApi
import android.location.Location
import android.os.Build
import android.util.Pair
import com.github.displace.sdp2022.gameComponents.Coordinates
import org.osmdroid.util.GeoPoint
import com.github.displace.sdp2022.util.math.Constants
import java.lang.IllegalArgumentException

/**
 * com.github.displace.sdp2022.util object for conversion between coordinates system (game com.github.displace.sdp2022.model abstraction 'Coordinates',
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
object CoordinatesConversionUtil {
    fun geoPoint(location : Location): GeoPoint {
        return GeoPoint(location.latitude,location.longitude)
    }

    fun geoPoint(coordinates: Coordinates): GeoPoint {
        if(!isValid(coordinates)) throw IllegalArgumentException("invalid geographic coordinates")
        return GeoPoint(
            coordinates.pos.first, coordinates.pos.second
        )
    }

    private fun isValid(coordinates: Coordinates): Boolean {
        return coordinates.pos.first in Constants.MIN_LATITUDE..Constants.MAX_LATITUDE
                && coordinates.pos.second in Constants.MIN_LONGITUDE..Constants.MAX_LONGITUDE
    }

    fun coordinates(geoPoint: GeoPoint): Coordinates {
        return object : Coordinates {
            override val pos: Pair<Double,Double> = Pair(geoPoint.latitude ,geoPoint.longitude )
        }
    }
}