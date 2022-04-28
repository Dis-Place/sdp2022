package com.github.displace.sdp2022.authentication

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.R
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TempLoginActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(TempLoginActivity::class.java)


    @Test
    fun signInButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignIn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*@Test
    fun checkboxIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.loginRememberCheckBox))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }*/

    @Test
    fun logoutButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun tryingToLogOutWhileNotLoggedInTest() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun guestSignInButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.guestSignInButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun readyButtonIsNotVisibleTest() {
        Espresso.onView(ViewMatchers.withId(R.id.goToAppOnlineButton))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

    }
}
