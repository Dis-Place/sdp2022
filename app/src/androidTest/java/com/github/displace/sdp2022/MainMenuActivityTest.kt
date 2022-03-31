package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.friends.Friend
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */
    @Test
    fun testingInput() {

        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.WelcomeText))
                .check(matches(withText("Welcome Baptou!")))
        }
    }

    @Test
    fun testProfileButton() {
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.profileButton)).perform(click())
        Espresso.onView(withId(R.id.profileUsername)).check(matches(withText("Name")))
    }

    @Test
    fun testSettingsButton() {
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.settingsButton)).perform(click())
        Espresso.onView(withId(R.id.textView4)).check(matches(withText("SETTINGS")))
    }

    @Test
    fun testNewsButton() {
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.newsButton)).perform(click())
        Espresso.onView(withId(R.id.textView)).check(matches(withText("NEWS")))
    }

}