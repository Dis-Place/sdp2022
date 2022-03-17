package com.github.blecoeur.bootcamp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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

    @Test
    fun testingInput() {
        //Intents.init()
        onView(ViewMatchers.withId(R.id.mainName))
            .perform(ViewActions.replaceText("baptou gaming")).perform(
                ViewActions.closeSoftKeyboard()
            )
        onView(ViewMatchers.withId(R.id.mainGoButton)).perform(click())

        //intended(IntentMatchers.hasExtra(EXTRA_MESSAGE, "baptou gaming"))

        //Intents.release()
    }

    @Test
    fun testingButton() {
        onView(withId(R.id.mainGoButton)).perform(click())
        onView(withId(R.id.editProfile)).check(matches(withText("Edit Profile")))
    }
}