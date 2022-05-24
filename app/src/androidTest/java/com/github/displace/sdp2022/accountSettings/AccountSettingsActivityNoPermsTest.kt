package com.github.displace.sdp2022.accountSettings

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.users.CompleteUser
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AccountSettingsActivityNoPermsTest {

    lateinit var completeUser: CompleteUser
    val intent =
        Intent(ApplicationProvider.getApplicationContext(), AccountSettingsActivity::class.java)
    val scenario: ActivityScenario<AccountSettingsActivity> = ActivityScenario.launch(intent)
    @Before
    fun before(){

        val app = ApplicationProvider.getApplicationContext() as MyApplication
        completeUser = CompleteUser(app,null)
        app.setActiveUser(completeUser)
        Thread.sleep(1000)
        MockDatabaseUtils.mockIntent(intent)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app,intent))
        Thread.sleep(100)
    }

    @After
    fun after() {
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun pictureDoesntUpdateWithoutCameraPermissions() {

        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Camera")).perform(click())
            // check Toast make text
            // check ImageView hasn't changed
            val device = UiDevice.getInstance(getInstrumentation())
            device.findObject(UiSelector().clickable(true).checkable(false).index(1)).click()
            device.findObject(UiSelector().clickable(true).checkable(false).index(1)).click()
            onView(withId(R.id.profilePic)).inRoot(not(isDialog())).check(matches(withTagKey(R.id.profilePic, `is`("defaultPicTag"))))

        }
    }

    @Test
    fun pictureDoesntUpdateWithoutStoragePermissions() {


        scenario.use {
            // Only clicks on the correct button but doesn't check if the picture changes or not
            onView(withId(R.id.profilePicUpdate)).perform(click())
            onView(withText("Gallery")).perform(click())
            // check Toast make text
            // check ImageView hasn't changed
            val device = UiDevice.getInstance(getInstrumentation())
            device.findObject(UiSelector().clickable(true).checkable(false).index(1)).click()
            onView(withId(R.id.profilePic)).inRoot(not(isDialog())).check(matches(withTagKey(R.id.profilePic, `is`("defaultPicTag"))))
        }
    }
}