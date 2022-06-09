package com.github.displace.sdp2022.news

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDatabaseUtils
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsActivityTest {

    @get:Rule
    val testRule = run {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SignInActivity::class.java).apply {
                putExtra("DEBUG", true)
            }

        DatabaseFactory.clearMockDB()

        MockDatabaseUtils.mockIntent(intent)

        ActivityScenarioRule<SignInActivity>(intent)
    }

    /*@After
    fun removeAnonymousUserFromDB() {
        (ApplicationProvider.getApplicationContext() as MyApplication).getActiveUser()
            ?.removeUserFromDatabase()
    }*/

    @Test
    fun startToNewsTest() {
        onView(withId(R.id.signInActivityGuestModeButton)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.newsButton)).perform(click())
        onView(withId(R.id.textView)).check(
            ViewAssertions.matches(
                withText("NEWS")
            )
        )
    }

}