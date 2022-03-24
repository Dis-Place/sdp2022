package com.github.displace.sdp2022.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginMenuActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LoginMenuActivity::class.java)

    @Test
    fun pressingGoogleLoginButtonLaunchActivity() {
        Intents.init()

        onView(withId(R.id.googleLoginButton)).perform(click())

        //TODO:Modify class name when it will exist
        intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun pressingNormalLoginButtonLaunchActivity() {
        Intents.init()

        onView(withId(R.id.normalLoginButton)).perform(click())

        //TODO:Modify class name when it will exist
        intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))

        Intents.release()
    }
}