package com.github.displace.sdp2022

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
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
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameMenuTest {
    @get:Rule
    val testRule = ActivityScenarioRule(com.github.displace.sdp2022.GameVersusViewActivity::class.java)
    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */

    @Test
    fun testPlayButton() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.TryText))
                .check(matches(withText("status : neutral, nombre d'essais restant : 4")))
        }
    }

    @Test
    fun testMap() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun testWinButton() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        }
    }

    @Test
    fun testFailButton() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Espresso.onView(withId(R.id.TryText))
                .check(matches(withText("status : fail, nombre d'essais restant : 3")))
        }
    }

    @Test
    fun testEndButton() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        }
    }

    @Test
    fun testQuitButton() {
        testRule.scenario.use {
            Espresso.onView(withId(R.id.closeButton)).perform(click())
            Intents.intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
            Intents.release()
        }
    }
}