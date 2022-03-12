package com.github.blecoeur.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsActivityNoPermsTest {

    @Test
    fun pictureDoesntUpdateWithoutCameraPermissions() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)

        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            Espresso.onView(ViewMatchers.withId(R.id.profilePicUpdate)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withText("Camera")).perform(ViewActions.click())
            // check Toast make text
            // check ImageView hasn't changed
        }
    }

    @Test
    fun pictureDoesntUpdateWithoutStoragePermissions() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)

        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            Espresso.onView(ViewMatchers.withId(R.id.profilePicUpdate)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withText("Gallery")).perform(ViewActions.click())
            // check Toast make text
            // check ImageView hasn't changed
        }
    }
}