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
class AddFriendActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(AddFriendActivity::class.java)

    @Test
    fun addButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.sendFriendRequestButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun editTextIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.friendRequestEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}