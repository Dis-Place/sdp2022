package com.github.displace.sdp2022

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.util.gps.GPSPositionUpdater
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoMapActivityTest {


    @get:Rule
    val testRule = ActivityScenarioRule(DemoMapActivity::class.java)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )


    /***
     * checks if the mapview is displayed
     */
    @Test
    fun mapIsDisplayedProperly() {
        testRule.scenario.use {
            onView(withId(R.id.map)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun longClickClickDoesNotCauseException() {
        testRule.scenario.use {
            onView(withId(R.id.map)).perform(longClick()).perform(longClick())
            onView(withId(R.id.map)).perform(longClick()).perform(click())
        }
    }

    @Test
    fun centerButtonDoesNotCrashApp() {
        testRule.scenario.use {
            onView(withId(R.id.centerGPS)).perform(click())
        }
    }

    @Test
    fun listenersIsEmptyOnStart() {
        val scenario = testRule.scenario
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size,0)
        }
    }

    @Test
    fun markersToggleAddsListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.markersToggleButton)).perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size,1)
        }
    }

    @Test
    fun toastPosToggleAddsListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.toastPosToggleButton)).perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size,1)
        }
    }

    @Test
    fun markersToggleRemovesListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.markersToggleButton))
            .perform(click())
            .perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size,0)
        }
    }

    @Test
    fun toastPosToggleRemovesListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.toastPosToggleButton))
            .perform(click())
            .perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size,0)
        }
    }

    @Test
    fun disableAllClearsListeners() {
        val scenario = testRule.scenario
        onView(withId(R.id.toastPosToggleButton))
            .perform(click())
        onView(withId(R.id.markersToggleButton))
            .perform(click())
        onView(withId(R.id.disableAllButton))
            .perform(click())
        scenario.onActivity { activity ->
            assertEquals(0,activity.mapViewListeners().size)
        }
    }

    @Test
    fun longClickClickWithListenersDoesNotCauseCrash(){
        testRule.scenario
        onView(withId(R.id.toastPosToggleButton))
            .perform(click())
        onView(withId(R.id.markersToggleButton))
            .perform(click())
        onView(withId(R.id.map))
            .perform(longClick())
            .perform(click())
    }
}