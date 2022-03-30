package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun darkModeSwitchIsDisplayedTest() {

        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun darkModeSwitchIsDisplayedAfterClickTest() {

        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun offlineModeSwitchIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun offlineModeSwitchIsDisplayedAfterClickTest() {
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun musicSwitchIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

    @Test
    fun musicSwitchIsDisplayedAfterClickTest() {
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun sFXSwitchIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun sFXSwitchIsDisplayedAfterClickTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}