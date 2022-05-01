package com.github.displace.sdp2022.profile.friendInvites

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendRequestTest {

    @get:Rule
    val testRule = ActivityScenarioRule(FriendRequest::class.java)


    @Test
    fun acceptButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.acceptRequestButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun rejectButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.rejectRequestButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun textViewIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.requestSourceText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }





}