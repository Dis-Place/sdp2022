package com.github.displace.sdp2022.map

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.DemoMapActivity
import com.github.displace.sdp2022.DemoMapActivity.Companion.MOCK_MARKERS_POSITIONS
import com.github.displace.sdp2022.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarkerManagerTest {
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
            val positions = a.mockPinpointsRef.get()
            assertEquals(MOCK_MARKERS_POSITIONS.size,positions.size)
            for(i in MOCK_MARKERS_POSITIONS.indices){
                assertEquals(MOCK_MARKERS_POSITIONS[i].latitude,positions[i].latitude,EPSILON)
                assertEquals(MOCK_MARKERS_POSITIONS[i].longitude,positions[i].longitude,EPSILON)
            }
        }

    }

    companion object{
        val MARKER_DESTROY_DELAY = 50.toLong()
        val EPSILON = 1e-4
    }

}