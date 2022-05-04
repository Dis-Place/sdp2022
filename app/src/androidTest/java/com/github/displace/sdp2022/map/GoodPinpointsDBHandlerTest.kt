package com.github.displace.sdp2022.map

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.DemoMapActivity
import com.github.displace.sdp2022.DemoMapActivity.Companion.MOCK_MARKERS_POSITIONS
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.map.PinpointsManagerTest.Companion.assertCorrectPositions
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoodPinpointsDBHandlerTest {

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

    @Before
    fun clear() {
        DatabaseFactory.clearMockDB()
    }

    @Test
    fun communicationIsSuccessfulOnNonEmptyPinpoints(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        scenario.onActivity { a ->
            assertCorrectPositions(MOCK_MARKERS_POSITIONS, a.mockPinpointsRef.get())
            assertCorrectPositions(MOCK_MARKERS_POSITIONS, a.remoteMockPinpointsRef.get())
        }
    }

    @Test
    fun communicationIsSuccessfulOnEmptyPinpoints(){
        val scenario = testRule.scenario
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        onView(ViewMatchers.withId(R.id.toggleMockMarkersButton)).perform(click())
        scenario.onActivity { a ->
            assertCorrectPositions(listOf(), a.mockPinpointsRef.get())
            assertCorrectPositions(listOf(), a.remoteMockPinpointsRef.get())
        }
    }

    companion object {
        val DB_DELAY = 2000.toLong()
    }



}