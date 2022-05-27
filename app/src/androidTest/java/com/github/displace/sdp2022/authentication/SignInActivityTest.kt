package com.github.displace.sdp2022.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.BundleMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.displace.sdp2022.DemoMapActivity
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.OfflineUserFetcher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    val app = ApplicationProvider.getApplicationContext() as MyApplication

    @get:Rule
    val activityScenarioRule = run {
        init()
        AuthFactory.setupMock("anything")
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SignInActivity::class.java)
        MockAuthUtils.mockIntent(intent)

        DatabaseFactory.clearMockDB()

        MockDatabaseUtils.mockIntent(intent)

        ActivityScenarioRule<SignInActivity>(intent)
    }


    @After
    fun after() {
        app.getActiveUser()?.removeUserFromDatabase()
    }

    @Test
    fun signInAsGuestWorks() {
        onView(withId(R.id.signInActivityGuestModeButton)).perform(click())
        intended(hasComponent(MainMenuActivity::class.java.name))
        onView(withId(R.id.welcomeText)).check(matches(withText(containsString("Guest"))))
        onView(withId(R.id.mainMenuLogOutButton)).perform(click())
        release()
    }

    @Test
    fun signInWithGoogleWorks() {
        onView(withId(R.id.signInActivitySignInButton)).perform(click())
        intended(toPackage("com.google.android.gms"))
        release()
    }


}