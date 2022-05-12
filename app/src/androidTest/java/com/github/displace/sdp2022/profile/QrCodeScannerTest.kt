package com.github.displace.sdp2022.profile

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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.qrcode.QrCodeScannerActivity
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class QrCodeScannerTest {

    lateinit var intent: Intent

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
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @Test
    fun testScannerFail() {

        ActivityScenario.launch<QrCodeScannerActivity>(intent).use {

            val resultData = Intent()
            resultData.data = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + ApplicationProvider.getApplicationContext<Context?>().resources.getResourcePackageName(
                    com.github.displace.sdp2022.R.drawable.ic_launcher_foreground
                )
                        + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceTypeName(
                    com.github.displace.sdp2022.R.drawable.ic_launcher_foreground
                )
                        + '/' + ApplicationProvider.getApplicationContext<Context?>().resources.getResourceEntryName(
                    com.github.displace.sdp2022.R.drawable.ic_launcher_foreground
                )
            )

            try {
                val expectedIntent = IntentMatchers.hasAction(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
                val response = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                Intents.intending(expectedIntent).respondWith(response)
                onView(ViewMatchers.withId(com.github.displace.sdp2022.R.id.friendsButton))
                    .perform(ViewActions.click())
                onView(withId(com.github.displace.sdp2022.R.id.scanQRButton)).perform(
                    ViewActions.click()
                )
                //check toast error message

            } finally {

            }

        }

    }


}