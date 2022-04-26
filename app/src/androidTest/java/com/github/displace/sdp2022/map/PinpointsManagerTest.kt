package com.github.displace.sdp2022.map

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.DemoMapActivity
import com.github.displace.sdp2022.DemoMapActivity.Companion.MOCK_MARKERS_POSITIONS
import com.github.displace.sdp2022.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

@RunWith(AndroidJUnit4::class)
class PinpointsManagerTest {
    @get:Rule
    val testRule = ActivityScenarioRule(DemoMapActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun playerPinPointsAreAddedToPlayerRef(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.markersToggleButton))
            .perform(click())
        onView(ViewMatchers.withId(R.id.map))
            .perform(longClick())
            .perform(swipeLeft())
            .perform(longClick())
        scenario.onActivity { a ->
            assertEquals(2,a.markerManager().playerPinPoints().size)
        }

    }

    @Test
    fun playerPinPointsAreRemovedFromPlayerRef(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.markersToggleButton))
            .perform(click())
        onView(ViewMatchers.withId(R.id.map))
            .perform(longClick())
            .perform(click())
        Thread.sleep(MARKER_DESTROY_DELAY)
        onView(ViewMatchers.withId(R.id.map))
            .perform(swipeLeft())
            .perform(longClick())
        scenario.onActivity { a ->
            assertEquals(1,a.markerManager().playerPinPoints().size)
        }

    }

    @Test
    fun addAllAddsCorrectPositionsToPinpointsRef(){
        val scenario = testRule.scenario

        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton))
            .perform(click())
        scenario.onActivity { a ->
            assertCorrectPositions(MOCK_MARKERS_POSITIONS,a.mockPinpointsRef.get())
        }

    }

    @Test
    fun clearRemovesAllPinpointsFromPinpointsRef(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton))
            .perform(click())
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton))
            .perform(click())
        Thread.sleep(MARKER_DESTROY_DELAY)
        scenario.onActivity { a ->
            assertCorrectPositions(listOf(),a.mockPinpointsRef.get())
        }
    }

    companion object{
        val MARKER_DESTROY_DELAY = 50.toLong()
        val EPSILON = 1e-4

        fun assertCorrectPositions(expected: List<GeoPoint>,actual: List<GeoPoint>){
            assertEquals(expected.size,actual.size)
            for(i in expected.indices){
                assertEquals(expected[i].latitude,actual[i].latitude,EPSILON)
                assertEquals(expected[i].longitude,actual[i].longitude,EPSILON)
            }
        }
    }

}