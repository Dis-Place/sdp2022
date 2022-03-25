package com.github.displace.sdp2022

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameMenuTest {
    @get:Rule
    val testRule = ActivityScenarioRule(com.github.displace.sdp2022.GameVersusViewActivity::class.java)

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */
/*
    @Test
    fun testPlayButton() {
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("neutral")))
    }


    @Test
    fun testWinButton() {
        Intents.init()
        Espresso.onView(withId(R.id.triButtonWin)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        Intents.release()
    }


    @Test
    fun testFailButton() {
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("fail")))
    }

    @Test
    fun testEndButton() {
        Intents.init()
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())
        Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun testQuitButton() {
        Intents.init()
        Espresso.onView(withId(R.id.closeButton)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
        Intents.release()
    }
    
 */
}