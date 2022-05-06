package com.github.displace.sdp2022.profile.friendInvites

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.GameSummaryActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.qrcode.QrCodeScannerActivity
import com.github.displace.sdp2022.profile.qrcode.QrCodeTemp
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendRequestTest {
//TODO: NOT SURE IF SHOULD KEEP THIS
//    lateinit var intent: Intent
//
//    @Before
//    fun setup() {
//        Intents.init()
//        intent = Intent(ApplicationProvider.getApplicationContext(), FriendRequest::class.java)
//    }
//
//    @After
//    fun releaseIntents() {
//        Intents.release()
//    }
//
//
//    @Test
//    fun acceptButtonIsDisplayedTest() {
//        ActivityScenario.launch<GameSummaryActivity>(intent).use {
//            Espresso.onView(ViewMatchers.withId(R.id.acceptRequestButton))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        }
//    }
//    @Test
//    fun rejectButtonIsDisplayedTest() {
//        ActivityScenario.launch<GameSummaryActivity>(intent).use {
//            Espresso.onView(ViewMatchers.withId(R.id.rejectRequestButton))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        }
//    }
//
//    @Test
//    fun textViewIsDisplayedTest() {
//        ActivityScenario.launch<GameSummaryActivity>(intent).use {
//            Espresso.onView(ViewMatchers.withId(R.id.requestSourceText))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        }
//    }



}