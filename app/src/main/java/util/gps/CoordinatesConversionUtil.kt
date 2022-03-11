package util.gps

import GameComponents.Coordinate
import android.location.Location
import org.osmdroid.util.GeoPoint
import util.math.Constants

object CoordinatesConversionUtil {
    fun ofLocation(location : Location): GeoPoint {
        return GeoPoint(location.latitude,location.longitude)
    }

    fun ofCoordinates(coordinates: Coordinate): GeoPoint {
        return GeoPoint(coordinates.pos[0],coordinates.pos[1])
    }

    fun isValid(coordinates: Coordinate): Boolean {
        return coordinates.pos.size == 2
                && coordinates.pos[0] in Constants.MIN_LONGITUDE..Constants.MAX_LONGITUDE
                && coordinates.pos[1] in Constants.MIN_LATITUDE..Constants.MAX_LATITUDE
    }
}