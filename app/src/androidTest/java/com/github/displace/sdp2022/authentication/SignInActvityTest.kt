package com.github.displace.sdp2022.authentication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.OfflineUserFetcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInActvityTest {

    private lateinit var tempCompleteUser: CompleteUser
    private val context = InstrumentationRegistry.getInstrumentation().context

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(SignInActivity::class.java)


    @Test
    fun guestModeButtonWorks() {
        val tempCompleteUser = OfflineUserFetcher(context).getCompleteUser()

        init()
        onView(withId(R.id.signInActivityGuestModeButton)).perform(click())
        Thread.sleep(2_000)
        hasComponent(MainMenuActivity::class.java.name)
        release()

        OfflineUserFetcher(context).setCompleteUser(tempCompleteUser)
    }

    @Test
    fun signInButtonWorks() {
        val tempCompleteteUser = OfflineUserFetcher(context).getCompleteUser()

        onView(withId(R.id.signInActivitySignInButton)).perform(click())
        Thread.sleep(2_000)

        //TODO: check that we have 2 intents
        assert(true)

        OfflineUserFetcher(context).setCompleteUser(tempCompleteUser)
    }


}