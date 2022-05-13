package com.github.displace.sdp2022.profile

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.*
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.qrcode.QrCodeScannerActivity
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeActivityTest {

    lateinit var intent: Intent

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA
    )

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app,null, false))
        Thread.sleep(3000)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app))
        Thread.sleep(1000)

        Intents.init()
        intent = Intent(ApplicationProvider.getApplicationContext(),ProfileActivity::class.java)
    }

    @After
    fun releaseIntents() {
        Intents.release()
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()?.removeUserFromDatabase()
    }

    @Test
    fun testShowButton() {

        ActivityScenario.launch<ProfileActivity>(intent).use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.showQRButton)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.fullimage))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


        }

    }

    @Test
    fun testScanButton() {
        ActivityScenario.launch<ProfileActivity>(intent).use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.scanQRButton)).perform(ViewActions.click())
            Intents.intended(IntentMatchers.hasComponent(QrCodeScannerActivity::class.java.name))
        }

    }


}