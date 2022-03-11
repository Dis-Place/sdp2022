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
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoMapActivityTest {

    /***
     * checks if the mapview is displayed
     */
    @Test
    fun mapIsDisplayedProperly()
    {
        val intent = Intent(getApplicationContext(), DemoMapActivity::class.java)
        val scenario = launch<DemoMapActivity>(intent)
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        scenario.close()
    }

}