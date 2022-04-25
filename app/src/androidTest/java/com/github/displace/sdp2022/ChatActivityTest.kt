package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatActivityTest {

   /* @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(null, false))

        Thread.sleep(3000)
    }


    @Test
    fun testChatView(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ChatActivity::class.java)
        val scenario = ActivityScenario.launch<ChatActivity>(intent)


        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.chatEditText)).perform(ViewActions.typeText("hh")).perform(ViewActions.closeSoftKeyboard())
            Espresso.onView(ViewMatchers.withId(R.id.sendChatMessage)).perform(ViewActions.click())
        }

    }*/

}