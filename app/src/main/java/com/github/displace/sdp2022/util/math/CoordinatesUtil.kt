package com.github.displace.sdp2022.util.math

import com.github.displace.sdp2022.gameComponents.Coordinates
import android.location.Location
import com.github.displace.sdp2022.gameComponents.Point
import org.osmdroid.util.GeoPoint
import java.lang.IllegalArgumentException

/**
 * Utility for coordinates conversion (model <-> gui map) and distance computation
 */
object CoordinatesUtil {

    /**
     * @param location
     * @return geoPoint corresponding to the Location
     */
    fun geoPoint(location : Location): GeoPoint {
        return GeoPoint(location.latitude,location.longitude)
    }

    /**
     * @param coordinates expected to be geographic
     * @return geoPoint corresponding to the coodinates
     * @throws IllegalArgumentException if the coordinates are not valid geographic coordinates
     */
    fun geoPoint(coordinates: Coordinates): GeoPoint {
        if(!isValid(coordinates)) throw IllegalArgumentException("invalid geographic coordinates")
        return GeoPoint(coordinates.pos.first, coordinates.pos.second)
    }

    /**
     * @param coordinates
     * @return validity as geographic coordinates
     */
    fun isValid(coordinates: Coordinates): Boolean {
        return coordinates.pos.first in Constants.MIN_LATITUDE..Constants.MAX_LATITUDE
                && coordinates.pos.second in Constants.MIN_LONGITUDE..Constants.MAX_LONGITUDE
    }

    /**
     * @param geoPoint
     * @return geographic coordinates (Point) corresponding to geoPoint
     */
    fun coordinates(geoPoint: GeoPoint): Coordinates {
        return Point(geoPoint.latitude,geoPoint.longitude)
    }

    /**
     * @param p1 geographic coordinates
     * @param p2 geographic coordinates
     * @return the distance between p1 and p2 in meters
     */
    fun distance(p1: Coordinates, p2: Coordinates): Double {
        return distance(geoPoint(p1), geoPoint(p2))
    }

    /**
     * @param p1
     * @param p2
     * @return the distance between p1 and p2 in meters
     */
    fun distance(p1: GeoPoint, p2: GeoPoint): Double {
        return p1.distanceToAsDouble(p2)
    }
}