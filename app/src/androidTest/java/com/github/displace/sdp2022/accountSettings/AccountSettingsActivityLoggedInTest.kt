package com.github.displace.sdp2022.accountSettings

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.authentication.TempLoginActivity
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsActivityLoggedInTest {
    /*@get:Rule
    val testRule = ActivityScenarioRule(AccountSettingsActivity::class.java)*/

    @get:Rule
    val testRule = ActivityScenarioRule(TempLoginActivity::class.java)

    @Before
    fun login() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), TempLoginActivity::class.java)
        val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
        scenario.use {
            onView(withId(R.id.guestSignInButton)).perform(click())
            onView(withId(R.id.profileButton)).perform(click())
            onView(withId(R.id.profileSettingsButton)).perform(click())
        }
    }

    @After
    fun logout() {
        pressBack()
        pressBack()
    }

    /*@Test
    fun passwordIsUpdated() {
        onView(withId(R.id.passwordUpdate)).perform(click())
        onView(withId(R.id.oldPasswordLog)).perform(
            replaceText("password"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.newPasswordLog)).perform(
            replaceText("wordpass"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordUpdateButton)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.actualPassword)).check(ViewAssertions.matches(ViewMatchers.withText("wordpass")))

        onView(withId(R.id.passwordUpdate)).perform(click())
        onView(withId(R.id.oldPasswordLog)).perform(
            replaceText("wordpass"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.newPasswordLog)).perform(
            replaceText("password"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordUpdateButton)).perform(click())
    }

    @Test
    fun passwordNotUpdatedIfOldPasswordIncorrect() {
        onView(withId(R.id.passwordUpdate)).perform(click())
        onView(withId(R.id.oldPasswordLog)).perform(
            replaceText("pass"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.newPasswordLog)).perform(
            replaceText("word"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordUpdateButton)).perform(click())
        onView(withId(R.id.actualPassword)).check(ViewAssertions.matches(ViewMatchers.withText("password")))
        // needs a Toast.maketext check
        //onView(withText("Incorrect password")).inRoot(withDecorView(not(getActivity(getApplicationContext())?.window?.decorView))).check(matches(isDisplayed()))
    }


    @Test
    fun passwordNotUpdateIfOldPasswordEmpty() {
        onView(withId(R.id.passwordUpdate)).perform(click())
        onView(withId(R.id.oldPasswordLog)).perform(
            replaceText(""),
            closeSoftKeyboard()
        )
        onView(withId(R.id.newPasswordLog)).perform(
            replaceText("word"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordUpdateButton)).perform(click())
        onView(withId(R.id.actualPassword)).check(ViewAssertions.matches(ViewMatchers.withText("password")))
        // needs a Toast.maketext check
    }

    @Test
    fun passwordNotUpdateIfNewPasswordEmpty() {
        onView(withId(R.id.passwordUpdate)).perform(click())
        onView(withId(R.id.oldPasswordLog)).perform(
            replaceText("password"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.newPasswordLog)).perform(
            replaceText(""),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordUpdateButton)).perform(click())
        onView(withId(R.id.actualPassword)).check(ViewAssertions.matches(ViewMatchers.withText("password")))
        // needs a Toast.maketext check
    }*/

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @Test
    fun pictureUpdatesCorrectlyFromGallery() {
        val resultData = Intent()
        resultData.data = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + ApplicationProvider.getApplicationContext<Context?>().resources.getResourcePackageName(
                R.drawable.ic_launcher_foreground
            )
                    + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceTypeName(
                R.drawable.ic_launcher_foreground
            )
                    + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceEntryName(
                R.drawable.ic_launcher_foreground
            )
        )

        Intents.init()
        try {
            val expectedIntent = IntentMatchers.hasAction(Intent.ACTION_PICK)
            val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
            Intents.intending(expectedIntent).respondWith(response)
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(ViewMatchers.withText("Gallery")).perform(click())
            Intents.intended(expectedIntent)
            // check that image is correctly updated
        } finally {
            Intents.release()
        }
    }

    @Test
    fun pictureUpdatesCorrectlyFromCamera() {
        val resultData = Intent()
        resultData.data = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + ApplicationProvider.getApplicationContext<Context?>().resources.getResourcePackageName(
                R.drawable.ic_launcher_foreground
            )
                    + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceTypeName(
                R.drawable.ic_launcher_foreground
            )
                    + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceEntryName(
                R.drawable.ic_launcher_foreground
            )
        )

        Intents.init()
        try {
            val expectedIntent = IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)
            val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
            Intents.intending(expectedIntent).respondWith(response)
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(ViewMatchers.withText("Camera")).perform(click())
            Intents.intended(expectedIntent)
            // check that image is correctly updated
        } finally {
            Intents.release()
        }
    }

    @Test
    fun usernameChangesCorrectly() {
        onView(withId(R.id.usernameUpdate)).perform(click())
        onView(withId(R.id.updateUsername)).perform(
            replaceText("newName"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.updateUsernameButton)).perform(click())
        onView(withId(R.id.username)).check(ViewAssertions.matches(ViewMatchers.withText("newName")))
    }

    @Test
    fun usernameNotUpdateIfEmpty() {
        onView(withId(R.id.usernameUpdate)).perform(click())
        onView(withId(R.id.updateUsername)).perform(
            replaceText(""),
            closeSoftKeyboard()
        )
        onView(withId(R.id.updateUsernameButton)).perform(click())
        onView(withId(R.id.username)).check(ViewAssertions.matches(ViewMatchers.withText(containsString("Guest"))))
    }
}