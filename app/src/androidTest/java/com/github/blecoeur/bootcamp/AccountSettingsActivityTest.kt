package com.github.blecoeur.bootcamp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.google.android.material.internal.ContextUtils.getActivity
import org.hamcrest.Matchers.not
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsActivityTest {

    @Test
    fun passwordIsUpdated() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.passwordUpdate)).perform(click())
            onView(withId(R.id.oldPasswordLog)).perform(replaceText("password"), closeSoftKeyboard())
            onView(withId(R.id.newPasswordLog)).perform(replaceText("pwd"), closeSoftKeyboard())
            onView(withId(R.id.passwordUpdateButton)).perform(click())
            onView(withId(R.id.actualPassword)).check(matches(withText("pwd")))
        }
    }

    @Test
    fun passwordNotUpdatedIfOldPasswordIncorrect() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.passwordUpdate)).perform(click())
            onView(withId(R.id.oldPasswordLog)).perform(replaceText("pass"), closeSoftKeyboard())
            onView(withId(R.id.newPasswordLog)).perform(replaceText("word"), closeSoftKeyboard())
            onView(withId(R.id.passwordUpdateButton)).perform(click())
            onView(withId(R.id.actualPassword)).check(matches(withText("password")))
            // needs a Toast.maketext check
        }
    }

    @Test
    fun passwordNotUpdateIfOldPasswordEmpty() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.passwordUpdate)).perform(click())
            onView(withId(R.id.oldPasswordLog)).perform(replaceText(""), closeSoftKeyboard())
            onView(withId(R.id.newPasswordLog)).perform(replaceText("word"), closeSoftKeyboard())
            onView(withId(R.id.passwordUpdateButton)).perform(click())
            onView(withId(R.id.actualPassword)).check(matches(withText("password")))
            // needs a Toast.maketext check
        }
    }

    @Test
    fun passwordNotUpdateIfNewPasswordEmpty() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.passwordUpdate)).perform(click())
            onView(withId(R.id.oldPasswordLog)).perform(replaceText("password"), closeSoftKeyboard())
            onView(withId(R.id.newPasswordLog)).perform(replaceText(""), closeSoftKeyboard())
            onView(withId(R.id.passwordUpdateButton)).perform(click())
            onView(withId(R.id.actualPassword)).check(matches(withText("password")))
            // needs a Toast.maketext check
        }
    }

    fun pictureDoesntUpdateWithoutCameraPermissions() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)

        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Camera")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        }
    }

    fun pictureDoesntUpdateWithoutStoragePermissions() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)

        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Gallery")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        }
    }

    @get:Rule val storagePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Test
    fun pictureUpdatesCorrectlyFromGallery() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture is updated
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Gallery")).inRoot(isDialog()).check(matches(isDisplayed())).perform(
                click())
        }
    }

    @get:Rule var cameraPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Test
    fun pictureUpdatesCorrectlyFromCamera() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {

            // Only clicks on the correct button but doesn't check if the picture is updated
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Camera")).inRoot(isDialog()).check(matches(isDisplayed())).perform(
                click())

        }
    }

    @Test
    fun changeUsernameDoesNothing(){
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.usernameUpdate)).perform(click())
        }

    }
}
