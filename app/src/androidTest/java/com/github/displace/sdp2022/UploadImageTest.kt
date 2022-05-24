package com.github.displace.sdp2022

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UploadImageTest {
 
    @get:Rule
    val testRule = ActivityScenarioRule(UploadImageActivity::class.java)

    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app,null, DatabaseFactory.MOCK_DB))

        Thread.sleep(3000)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app))
        Thread.sleep(1000)
    }

    /*@After
    fun releaseIntents() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        //app.getActiveUser()?.removeUserFromDatabase()
    }*/

    @Test
    fun canSearchImage() {
        Intents.init()
        Espresso.onView(withId(R.id.imageUpload)).perform(click())
        intended(IntentMatchers.anyIntent())
        Intents.release()
    }


    @Test
    fun canUploadImage() {
        Intents.init()
        Espresso.onView(withId(R.id.uploadButton)).perform(click())
        intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))
        Intents.release()
    }

}