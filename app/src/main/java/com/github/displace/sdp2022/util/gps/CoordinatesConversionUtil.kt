package com.github.displace.sdp2022.util.gps

import com.github.displace.sdp2022.gameComponents.Coordinates
import android.location.Location
import com.github.displace.sdp2022.gameComponents.Point
import org.osmdroid.util.GeoPoint
import com.github.displace.sdp2022.util.math.Constants
import org.osmdroid.util.PointAccepter
import java.lang.IllegalArgumentException

/**
 * com.github.displace.sdp2022.util object for conversion between coordinates system (game com.github.displace.sdp2022.model abstraction 'Coordinates',
 */
object CoordinatesConversionUtil {
    fun geoPoint(location : Location): GeoPoint {
        return GeoPoint(location.latitude,location.longitude)
    }

    fun geoPoint(coordinates: Coordinates): GeoPoint {
        if(!isValid(coordinates)) throw IllegalArgumentException("invalid geographic coordinates")
        return GeoPoint(coordinates.pos.first, coordinates.pos.second)
    }

    fun isValid(coordinates: Coordinates): Boolean {
        return coordinates.pos.first in Constants.MIN_LATITUDE..Constants.MAX_LATITUDE
                && coordinates.pos.second in Constants.MIN_LONGITUDE..Constants.MAX_LONGITUDE
    }

    fun coordinates(geoPoint: GeoPoint): Coordinates {
        return Point(geoPoint.latitude,geoPoint.longitude)
    }
}