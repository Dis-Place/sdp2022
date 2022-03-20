package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import displace.sdp2022.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Test
    fun darkModeSwitchIsDisplayedTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun darkModeSwitchIsDisplayedAfterClickTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun offlineModeSwitchIsDisplayedTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun offlineModeSwitchIsDisplayedAfterClickTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.offlineModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun musicSwitchIsDisplayedTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun musicSwitchIsDisplayedAfterClickTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.musicSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun sFXSwitchIsDisplayedTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun sFXSwitchIsDisplayedAfterClickTest() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        val scenario = ActivityScenario.launch<SettingsActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.sFXSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}