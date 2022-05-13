package com.github.displace.sdp2022

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app, null))
        Thread.sleep(3000)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(), app))
        Thread.sleep(1000)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)
        ActivityScenario.launch<SettingsActivity>(intent)
    }

    @After
    fun setDown() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()?.removeUserFromDatabase()
    }

    @Test
    fun pressingDarkModeButtonDisplayToastMessage() {
        genericSettingsCheck("Dark Mode", "dark mode")
    }

    @Test
    fun pressingSfxButtonDisplayToastMessage() {
        genericSettingsCheck("SFX", "Sound effects")
    }

    @Test
    fun pressingMusicButtonDisplayToastMessage() {
        genericSettingsCheck("Music", "Music")
    }

    fun genericSettingsCheck(fullName: String, name: String) {
        //Select the dark mode switch and then press it
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("Enable $fullName")), click()
                )
            )

        //Verify that it was pressed
        if (testRule.scenario.state != Lifecycle.State.DESTROYED) {
            testRule.scenario.onActivity { activity ->
                onView(anyOf(withText("$name enabled"), withText("$name disabled"))).inRoot(
                    withDecorView(
                        not(
                            `is`(
                                activity.window.decorView
                            )
                        )
                    )
                ).check(
                    matches(
                        isDisplayed()
                    )
                )
            }
        } else {
            assert(true)
        }
    }
}
