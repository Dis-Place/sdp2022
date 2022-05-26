package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDB
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DemoMapActivityTest {


    @get:Rule
    val testRule = run {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), DemoMapActivity::class.java)

        // THIS IS YOU CLEAR THE MOCK DB
        DatabaseFactory.clearMockDB()

        // THIS IS HOW YOU MOCK THE DB IN TESTS
        MockDatabaseUtils.mockIntent(intent)

        // THIS IS HOW YOU MOCK THE GPS
        MockGPS.specifyMock(intent,MapViewManager.DEFAULT_CENTER)

        ActivityScenarioRule<DemoMapActivity>(intent)
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

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
            onView(withId(R.id.toggleGPSButton)).perform(click())
            onView(withId(R.id.centerGPS)).perform(click())
        }
    }


    @Test
    fun listenersIsEmptyOnStart() {
        val scenario = testRule.scenario
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size, 0)
        }
    }

    @Test
    fun markersToggleAddsListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.markersToggleButton)).perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size, 1)
        }
    }

    @Test
    fun toastPosToggleAddsListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.toastPosToggleButton)).perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size, 1)
        }
    }

    @Test
    fun markersToggleRemovesListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.markersToggleButton))
            .perform(click())
            .perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size, 0)
        }
    }

    @Test
    fun toastPosToggleRemovesListener() {
        val scenario = testRule.scenario
        onView(withId(R.id.toastPosToggleButton))
            .perform(click())
            .perform(click())
        scenario.onActivity { activity ->
            assertEquals(activity.mapViewListeners().size, 0)
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
            assertEquals(0, activity.mapViewListeners().size)
        }
    }

    @Test
    fun longClickClickWithListenersDoesNotCauseCrash() {
        testRule.scenario
        onView(withId(R.id.toastPosToggleButton))
            .perform(click())
        onView(withId(R.id.markersToggleButton))
            .perform(click())
        onView(withId(R.id.map))
            .perform(longClick())
            .perform(click())
    }

    @Test
    fun clickTwiceOnGPSToggleDoesNotCrashApp() {
        testRule.scenario
        onView(withId(R.id.toggleGPSButton))
            .perform(click())
            .perform(click())
    }

}