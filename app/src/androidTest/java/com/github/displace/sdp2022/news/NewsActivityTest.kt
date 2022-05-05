package com.github.displace.sdp2022.news

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.map.GoodPinpointsDBHandlerTest.Companion.DB_DELAY
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @After
    fun removeAnonymousUserFromDB() {
        (ApplicationProvider.getApplicationContext() as MyApplication).getActiveUser()?.removeUserFromDatabase()
    }

    @Test
    fun startToNewsTest() {
        Espresso.onView(ViewMatchers.withId(R.id.guestSignInButton)).perform(ViewActions.click())
        Thread.sleep(DB_DELAY)
        Espresso.onView(ViewMatchers.withId(R.id.goToAppOnlineButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.newsButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.textView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("NEWS")
            )
        )
    }

}