package com.github.displace.sdp2022.news

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainActivity
import displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun startToNewsTest() {
        Espresso.onView(ViewMatchers.withId(R.id.mainGoButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.newsButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.textView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("NEWS")
            )
        )
    }

}