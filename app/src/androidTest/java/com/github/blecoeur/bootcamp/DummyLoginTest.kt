package com.github.blecoeur.bootcamp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val NAME = "Jean Valjean"

@RunWith(AndroidJUnit4::class)
class DummyLoginTest {
    @get:Rule
    val testRule = ActivityScenarioRule(DummyLoginActivity::class.java)

    @Test
    fun testSendingUserHandleErrorsCorrectly() {
        Intents.init()
        val nanString = "Jean Michel Dupont"
        onView(ViewMatchers.withId(R.id.dummyName)).perform(ViewActions.replaceText(NAME))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyAge)).perform(ViewActions.replaceText(nanString))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyButtonSendUser)).perform(click())

        intended(IntentMatchers.hasExtra(EXTRA_USER, DummyUser(NAME, 0)))

        Intents.release()
    }

    @Test
    fun testSendingUserWorksCorrectly() {
        Intents.init()
        onView(ViewMatchers.withId(R.id.dummyName)).perform(ViewActions.replaceText(NAME))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyAge)).perform(ViewActions.replaceText("12"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyButtonSendUser)).perform(click())

        intended(IntentMatchers.hasExtra(EXTRA_USER, DummyUser(NAME, 12)))

        Intents.release()
    }
}