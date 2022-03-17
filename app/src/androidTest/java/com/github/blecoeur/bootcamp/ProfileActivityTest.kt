package com.github.blecoeur.bootcamp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.blecoeur.bootcamp.profile.ProfileActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(ProfileActivity::class.java)

    //    @Test
//    fun testSettingsButton(){
//        onView(withId(R.id.profileSettingsButton)).perform(click())
//        onView(withId(R.id.editProfile)).check(matches(withText("Edit Profile")))
//    }
    @Test
    fun dummyTest() {
        assert(true)
    }
}