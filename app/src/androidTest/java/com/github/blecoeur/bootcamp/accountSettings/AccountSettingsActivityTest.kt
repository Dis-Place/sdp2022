package com.github.blecoeur.bootcamp.accountSettings

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.blecoeur.bootcamp.AccountSettingsActivity
import com.github.blecoeur.bootcamp.ProfileActivity
import com.github.blecoeur.bootcamp.R
import com.google.firebase.auth.FirebaseAuth
import org.junit.BeforeClass
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
            //onView(withText("Incorrect password")).inRoot(withDecorView(not(getActivity(getApplicationContext())?.window?.decorView))).check(matches(isDisplayed()))
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

    @get:Rule val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Test
    fun pictureUpdatesCorrectlyFromGallery() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            val resultData = Intent()
            resultData.data = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + getApplicationContext<Context?>().resources.getResourcePackageName(R.drawable.ic_launcher_foreground)
                    + '/' + getApplicationContext<Context?>().resources.getResourceTypeName(R.drawable.ic_launcher_foreground)
                    + '/' + getApplicationContext<Context?>().resources.getResourceEntryName(R.drawable.ic_launcher_foreground))

            Intents.init()
            try {
                val expectedIntent = hasAction(Intent.ACTION_PICK)
                val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                intending(expectedIntent).respondWith(response)
                onView(withId(R.id.profilePicUpdate)).perform(click())
                onView(withText("Gallery")).perform(click())
                /*sleep(3000)
                val device = UiDevice.getInstance(getInstrumentation())
                val allowPermissions = device.findObject(UiSelector().clickable(true).checkable(false).textMatches("Allow"))
                if(allowPermissions.exists()) {
                    allowPermissions.click()
                }*/
                intended(expectedIntent)
                // check that image is correctly updated
            } finally {
                Intents.release()
            }
        }

    }

    @Test
    fun pictureUpdatesCorrectlyFromCamera() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            val resultData = Intent()
            resultData.data = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + getApplicationContext<Context?>().resources.getResourcePackageName(R.drawable.ic_launcher_foreground)
                    + '/' + getApplicationContext<Context?>().resources.getResourceTypeName(R.drawable.ic_launcher_foreground)
                    + '/' + getApplicationContext<Context?>().resources.getResourceEntryName(R.drawable.ic_launcher_foreground))

            Intents.init()
            try {
                val expectedIntent = hasAction(MediaStore.ACTION_IMAGE_CAPTURE)
                val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                intending(expectedIntent).respondWith(response)
                onView(withId(R.id.profilePicUpdate)).perform(click())
                onView(withText("Camera")).perform(click())
                intended(expectedIntent)
                // check that image is correctly updated
            } finally {
                Intents.release()
            }
        }
    }

    @Test
    fun usernameChangesCorrectly(){
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.usernameUpdate)).perform(click())
            onView(withId(R.id.updateUsername)).perform(replaceText("newName"), closeSoftKeyboard())
            onView(withId(R.id.updateUsernameButton)).perform(click())
            onView(withId(R.id.username)).check(matches(withText("newName")))
        }
    }

    @Test
    fun usernameNotUpdateIfEmpty() {
        val intent = Intent(getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.usernameUpdate)).perform(click())
            onView(withId(R.id.updateUsername)).perform(replaceText(""), closeSoftKeyboard())
            onView(withId(R.id.updateUsernameButton)).perform(click())
            onView(withId(R.id.username)).check(matches(withText("Name")))
        }
    }
}
