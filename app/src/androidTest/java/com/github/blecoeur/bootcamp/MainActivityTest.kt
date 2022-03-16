package com.github.blecoeur.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    //the communication between the activities is done through the preferences, Intents are no longer used
    @Test
    fun testingInput() {
    //    Intents.init()
        onView(ViewMatchers.withId(R.id.mainName))
            .perform(ViewActions.replaceText("baptou gaming")).perform(
                ViewActions.closeSoftKeyboard()
            )
        onView(ViewMatchers.withId(R.id.mainGoButton)).perform(click())

        onView(ViewMatchers.withId(R.id.WelcomeText)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Welcome baptou gaming!")
            )
        )

    //    intended(IntentMatchers.hasExtra(EXTRA_MESSAGE, "baptou gaming"))

    //    Intents.release()
    }

    @Test
    fun mapButtonGoesToDemoMapActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        val scenario = ActivityScenario.launch<MainActivity>(intent)
        onView(ViewMatchers.withId(R.id.mapButton)).perform(click())
        onView(ViewMatchers.withId(R.id.map)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

}