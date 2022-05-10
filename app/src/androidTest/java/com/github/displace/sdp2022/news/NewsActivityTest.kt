package com.github.displace.sdp2022.news

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.map.GoodPinpointsDBHandlerTest.Companion.DB_DELAY
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(SignInActivity::class.java)

    @After
    fun removeAnonymousUserFromDB() {
        (ApplicationProvider.getApplicationContext() as MyApplication).getActiveUser()
            ?.removeUserFromDatabase()
    }

    @Test
    fun startToNewsTest() {
        onView(withId(R.id.signInActivityGuestModeButton)).perform(click())
        Thread.sleep(DB_DELAY)
        onView(withId(R.id.newsButton)).perform(click())
        onView(withId(R.id.textView)).check(
            ViewAssertions.matches(
                withText("NEWS")
            )
        )
    }

}