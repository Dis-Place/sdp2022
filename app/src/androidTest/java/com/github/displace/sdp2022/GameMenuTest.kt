package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import androidx.test.rule.GrantPermissionRule
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameMenuTest {

    lateinit var intent: Intent

    @Before
    fun setup() {
        DatabaseFactory.clearMockDB()
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app,null, DatabaseFactory.MOCK_DB))

        Intents.init()
        intent = Intent(ApplicationProvider.getApplicationContext(),GameListActivity::class.java).apply {
            putExtra("DEBUG", true)
        }

        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app,intent))
        Thread.sleep(1000)
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun testPlayButton() {
        ActivityScenario.launch<GameListActivity>(intent).use {
            onView(withId(R.id.playVersusButton)).perform(ViewActions.click())
            Intents.intended(IntentMatchers.hasComponent(MatchMakingActivity::class.java.name))
        }
    }

    @Test
    fun testPlay2Button() {
        ActivityScenario.launch<GameListActivity>(intent).use {
            onView(withId(R.id.playVersusButton2)).perform(ViewActions.click())
            Intents.intended(IntentMatchers.hasComponent(MatchMakingActivity::class.java.name))
        }
    }

    @Test
    fun testPlay3Button() {
        ActivityScenario.launch<GameListActivity>(intent).use {
            onView(withId(R.id.playVersusButton4)).perform(ViewActions.click())
            Intents.intended(IntentMatchers.hasComponent(MatchMakingActivity::class.java.name))
        }
    }

}