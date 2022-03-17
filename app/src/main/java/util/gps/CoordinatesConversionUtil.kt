package util.gps

import GameComponents.Coordinates
import android.location.Location
import org.osmdroid.util.GeoPoint
import util.math.Constants
import java.lang.IllegalArgumentException

/**
 * util object for conversion between coordinates system (game model abstraction 'Coordinates',
 */
object CoordinatesConversionUtil {
    fun ofLocation(location : Location): GeoPoint {
        return GeoPoint(location.latitude,location.longitude)
    }

    fun ofCoordinates(coordinates: Coordinates): GeoPoint {
        if(!isValid(coordinates)) throw IllegalArgumentException("invalid geographic coordinates")
        return GeoPoint(coordinates.pos.first,coordinates.pos.second)
    }

    fun isValid(coordinates: Coordinates): Boolean {
        return coordinates.pos.first in Constants.MIN_LATITUDE..Constants.MAX_LATITUDE
                && coordinates.pos.second in Constants.MIN_LONGITUDE..Constants.MAX_LONGITUDE
    }

    fun ofGeoPoint(geoPoint: GeoPoint): Coordinates {
        return object : Coordinates {
            override val pos: Pair<Double,Double> = Pair(geoPoint.latitude,geoPoint.longitude)
        }
    }
}