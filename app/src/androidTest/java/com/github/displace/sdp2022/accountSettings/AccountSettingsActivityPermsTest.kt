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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.users.CompleteUser
import org.hamcrest.CoreMatchers
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsActivityPermsTest {
    @Before
    fun login() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        DatabaseFactory.clearMockDB()
        app.setActiveUser(CompleteUser(app, null, DatabaseFactory.MOCK_DB))
    }

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
            val intent =
                Intent(
                    ApplicationProvider.getApplicationContext(),
                    AccountSettingsActivity::class.java
                )
            val app = ApplicationProvider.getApplicationContext() as MyApplication
            MockDatabaseUtils.mockIntent(intent)
            app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app,intent))
            Thread.sleep(100)
            val scenario = ActivityScenario.launch<AccountSettingsActivity>(intent)
            scenario.use {
                val expectedIntent = IntentMatchers.hasAction(Intent.ACTION_PICK)
                val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                Intents.intending(expectedIntent).respondWith(response)
                onView(withId(R.id.profilePicUpdate)).perform(click())
                onView(ViewMatchers.withText("Gallery")).perform(click())
                Intents.intended(expectedIntent)
                // check that image is correctly updated
                onView(withId(R.id.profilePic)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withTagKey(
                            R.id.profilePic,
                            CoreMatchers.`is`("modifiedTag")
                        )
                    )
                )
            }
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
            val intent =
                Intent(
                    ApplicationProvider.getApplicationContext(),
                    AccountSettingsActivity::class.java
                )
            val scenario = ActivityScenario.launch<AccountSettingsActivity>(intent)
            scenario.use {
                val expectedIntent = IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)
                val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                Intents.intending(expectedIntent).respondWith(response)
                onView(withId(R.id.profilePicUpdate)).perform(click())
                onView(ViewMatchers.withText("Camera")).perform(click())
                Intents.intended(expectedIntent)
                // check that image is correctly updated

                onView(withId(R.id.profilePic)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withTagKey(
                            R.id.profilePic,
                            CoreMatchers.`is`("modifiedTag")
                        )
                    )
                )
            }
        } finally {
            Intents.release()
        }
    }

    @Test
    fun usernameChangesCorrectly() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario = ActivityScenario.launch<AccountSettingsActivity>(intent)
        scenario.use {
            onView(withId(R.id.usernameUpdate)).perform(click())
            onView(withId(R.id.updateUsername)).perform(
                replaceText("newName"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.updateUsernameButton)).perform(click())
            onView(withId(R.id.username)).check(ViewAssertions.matches(ViewMatchers.withText("newName")))
        }
    }

    @Test
    fun usernameNotUpdateIfEmpty() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AccountSettingsActivity::class.java)
        val scenario = ActivityScenario.launch<AccountSettingsActivity>(intent)
        scenario.use {
            onView(withId(R.id.usernameUpdate)).perform(click())
            onView(withId(R.id.updateUsername)).perform(
                replaceText(""),
                closeSoftKeyboard()
            )
            onView(withId(R.id.updateUsernameButton)).perform(click())
            onView(withId(R.id.username)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        containsString("defaultName")
                    )
                )
            )
        }
    }
}