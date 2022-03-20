package com.github.displace.sdp2022

import com.github.displace.sdp2022.util.gps.CoordinatesConversionUtil
import com.github.displace.sdp2022.util.math.Constants
import gameComponents.Coordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.osmdroid.util.GeoPoint

class ConvertionsUtilTest {
    private val DELTA = 1e-4

    private class DummyCoordinates(override val pos: Pair<Double, Double>) : Coordinates

    @Test
    fun geoPointIsCorrectOnValidCoordinates() {
        val coordinates = DummyCoordinates(Pair(1.8, 3.0))
        val geoPoint = CoordinatesConversionUtil.geoPoint(coordinates)
        assertEquals(coordinates.pos.first, geoPoint.latitude, DELTA)
        assertEquals(coordinates.pos.second, geoPoint.longitude, DELTA)
    }

    @Test
    fun geoPointThrowIllegalArgumentExceptionOnInvalidCoordinates() {
        var coordinates = DummyCoordinates(Pair(Constants.MAX_LATITUDE + 1, 3.0))
        assertThrows(IllegalArgumentException::class.java) {
            CoordinatesConversionUtil.geoPoint(coordinates)
        }
        coordinates = DummyCoordinates(Pair(Constants.MAX_LATITUDE, Constants.MAX_LONGITUDE + 1))
        assertThrows(IllegalArgumentException::class.java) {
            CoordinatesConversionUtil.geoPoint(coordinates)
        }

    }

    @Test
    fun coordinatesIsCorrectOnGeoPoint() {
        val geoPoint = GeoPoint(3.0, 5.0)
        val coordinates = CoordinatesConversionUtil.coordinates(geoPoint)
        assertEquals(geoPoint.latitude, coordinates.pos.first, DELTA)
        assertEquals(geoPoint.longitude, coordinates.pos.second, DELTA)
    }
}