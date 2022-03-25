package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

//@RunWith(AndroidJUnit4::class)
@RunWith(MockitoJUnitRunner::class)
class TempLoginActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(TempLoginActivity::class.java)

    val intent = Intent(ApplicationProvider.getApplicationContext(), TempLoginActivity::class.java)

    // to put in test
//    val scenario = ActivityScenario.launch<TempLoginActivity>(intent)

    @Test
    fun signInButtonIsDisplayedTest() {

        val scenario = ActivityScenario.launch<TempLoginActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignIn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun logoutButtonIsDisplayedTest() {

        val scenario = ActivityScenario.launch<TempLoginActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    @Test
    fun tryingToLogOutWhileNotLoggedInTest(){
        val scenario = ActivityScenario.launch<TempLoginActivity>(intent)
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignOut))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}