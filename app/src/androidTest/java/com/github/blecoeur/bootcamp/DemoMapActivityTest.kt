package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.views.MapView

@RunWith(AndroidJUnit4::class)
class DemoMapActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(DemoMapActivity::class.java)

    /***
     * checks if the mapview is displayed
     */
    @Test
    fun mapIsDisplayedProperly()
    {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun longClickClickDoesNotCauseException()
    {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.map)).perform(click())
    }

    @Test
    fun centerButtonDoesNotCrashApp()
    {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.centerGPS)).perform(click())

    }

}