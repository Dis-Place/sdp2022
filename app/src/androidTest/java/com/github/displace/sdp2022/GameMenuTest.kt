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
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.map.MapViewManager
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameMenuTest {

    lateinit var intent: Intent
    val db = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as com.github.displace.sdp2022.RealTimeDatabase

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app,null))
        Thread.sleep(3000)


        Intents.init()
        intent = Intent(ApplicationProvider.getApplicationContext(),GameVersusViewActivity::class.java)
        MockGPS.specifyMock(intent, MOCK_GPS_POSITION)
        MockDatabaseUtils.mockIntent(intent)

        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app,intent))
        Thread.sleep(1000)
    }

    @After
    fun releaseIntents() {
        Intents.release()
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()?.removeUserFromDatabase()
    }


    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun testPlayButton() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.TryText))
                .check(matches(withText("remaining tries : 4")))
        }

        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()
    }

    @Test
    fun testMap() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            onView(withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()))
        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()

    }

    @Test
    fun testEndButton() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("dist",100000)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.centerButton)).perform(ViewActions.click())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())

            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))

        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()
    }

    @Test
    fun testFailButton() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("dist",100000)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.TryText))
                .check(matches(withText("wrong guess, remaining tries : 3")))

        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()

    }

    @Test
    fun testWinButton() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            //Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()

    }

    @Test
    fun testQuitButton() {
        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("gameMode","Versus")

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.closeButton)).perform(click())
            Intents.intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()
    }

    private fun swipeUp(): ViewAction? {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.TOP_CENTER, Press.FINGER
        )
    }

    companion object {
        val MOCK_GPS_POSITION = MapViewManager.DEFAULT_CENTER
    }

    @Test
    fun testChatButton() {

        intent.putExtra("gid","GameVersusTest")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("gameMode","Versus")
        ActivityScenario.launch<GameSummaryActivity>(intent).use {

            onView(withId(R.id.chatButton)).perform(click())
            onView(withId(R.id.chatEditText)).perform(typeText("hh")).perform(closeSoftKeyboard())
            Thread.sleep(1000)
            onView(withId(R.id.sendChatMessage)).perform(click())
            onView(withId(R.id.button4)).perform(click())

        }
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()!!.removeUserFromDatabase()
    }

}