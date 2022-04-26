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
import com.github.displace.sdp2022.map.PinpointsManagerTest.Companion.assertCorrectPositions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PinpointsDBCommunicationHandlerTest {
    @get:Rule
    val testRule = ActivityScenarioRule(DemoMapActivity::class.java)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun communicationIsSuccessfulOnNonEmptyPinpoints(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.DBButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.updateButton)).perform(click())
        Thread.sleep(DB_DELAY)
        scenario.onActivity { a ->
            assertCorrectPositions(MOCK_MARKERS_POSITIONS, a.mockPinpointsRef.get())
            assertCorrectPositions(MOCK_MARKERS_POSITIONS, a.remoteMockPinpointsRef.get())
        }
    }

    @Test
    fun communicationIsSuccessfulOnEmptyPinpoints(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.DBButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.updateButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(ViewMatchers.withId(R.id.updateButton)).perform(click())
        Thread.sleep(DB_DELAY)
        scenario.onActivity { a ->
            assertCorrectPositions(listOf(), a.mockPinpointsRef.get())
            assertCorrectPositions(listOf(), a.remoteMockPinpointsRef.get())
        }
    }

    companion object {
        val DB_DELAY = 2000.toLong()
    }



}