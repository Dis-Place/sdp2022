package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameMenuTest {

    lateinit var intent: Intent
    val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as com.github.displace.sdp2022.RealTimeDatabase

    @Before
    fun setup() {
        Intents.init()
        intent = Intent(ApplicationProvider.getApplicationContext(),GameVersusViewActivity::class.java)
        MockGPS.specifyMock(intent, MOCK_GPS_POSITION)
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun testPlayButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.TryText))
                .check(matches(withText("remaining tries : 4")))
        }
    }

    @Test
    fun testMap() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            onView(withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()))
        }

    }

    @Test
    fun testEndButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        }
    }

    @Test
    fun testFailButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.TryText))
                .check(matches(withText("wrong guess, remaining tries : 3")))
        }

    }

    @Test
    fun testWinButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        }

    }

    @Test
    fun testQuitButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.closeButton)).perform(click())
            Intents.intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
        }
    }

    private fun swipeUp(): ViewAction? {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.TOP_CENTER, Press.FINGER
        )
    }

    companion object {
        val MOCK_GPS_POSITION = MapViewManager.DEFAULT_CENTER
    }

}