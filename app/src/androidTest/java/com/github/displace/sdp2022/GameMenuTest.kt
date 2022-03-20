package com.github.displace.sdp2022

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.blecoeur.bootcamp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameMenuTest {
    @get:Rule
    val testRule = ActivityScenarioRule(com.github.displace.sdp2022.GameListActivity::class.java)

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */

    @Test
    fun testPlayButton(){
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("neutral")))
    }
    @Test
    fun testWinButton(){
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonWin)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("win")))
    }
    @Test
    fun testFailButton(){
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("fail")))
    }
    @Test
    fun testEndButton(){
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("end of game")))
    }

    @Test
    fun testQuitButton(){
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.closeButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("neutral")))
    }
}