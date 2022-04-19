package com.github.displace.sdp2022.users

import com.github.displace.sdp2022.gameComponents.Coordinates
import com.github.displace.sdp2022.gameComponents.Point
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.osmdroid.util.GeoPoint
import com.github.displace.sdp2022.util.math.Constants
import com.github.displace.sdp2022.util.math.CoordinatesUtil.coordinates
import com.github.displace.sdp2022.util.math.CoordinatesUtil.distance
import com.github.displace.sdp2022.util.math.CoordinatesUtil.geoPoint
import java.lang.IllegalArgumentException

class CoordinatesUtilTest {
    private val DELTA_GEO = 1e-6
    private val DELTA_METERS = 1e-1

    private class DummyCoordinates(override val pos: Pair<Double, Double>) : Coordinates

    @Test
    fun geoPointIsCorrectOnValidCoordinates(){
        val coordinates = DummyCoordinates(Pair(1.8,3.0))
        val geoPoint = geoPoint(coordinates)
        assertEquals(coordinates.pos.first,geoPoint.latitude,DELTA_GEO)
        assertEquals(coordinates.pos.second,geoPoint.longitude,DELTA_GEO)
    }

    @Test
    fun geoPointThrowIllegalArgumentExceptionOnInvalidCoordinates(){
        var coordinates = DummyCoordinates(Pair(Constants.MAX_LATITUDE + 1,3.0))
        assertThrows(IllegalArgumentException::class.java) {
            geoPoint(coordinates)
        }
        coordinates = DummyCoordinates(Pair(Constants.MAX_LATITUDE,Constants.MAX_LONGITUDE+1))
        assertThrows(IllegalArgumentException::class.java) {
            geoPoint(coordinates)
        }

    }

    @Test
    fun coordinatesIsCorrectOnGeoPoint(){
        val geoPoint = GeoPoint(3.0,5.0)
        val coordinates = coordinates(geoPoint)
        assertEquals(geoPoint.latitude,coordinates.pos.first,DELTA_GEO)
        assertEquals(geoPoint.longitude,coordinates.pos.second,DELTA_GEO)
    }

    @Test
    fun distanceIsWellBehavedOnDuplicatePoint() {
        val point = Point(6.0,34.0)
        assertEquals(0.0, distance(point,point), DELTA_GEO)
    }

    @Test
    fun distanceIsConsistantBetweenGeoPointsAndPoints() {
        val testPoints = listOf(
            Pair(Point(70.0,-74.04),Point(-65.87,-77.03)),
            Pair(Point(-5.0,-0.0004),Point(89.7,56.0)),
            Pair(Point(79.0,-164.04),Point(-87.2,-78.03)),
            Pair(Point(-78.5,-0.054),Point(-78.5007,-0.054008))
        )

        for(pts in testPoints) {
            assertEquals(distance(pts.first,pts.second),distance(geoPoint(pts.first), geoPoint(pts.second)), DELTA_METERS)
        }
    }

}