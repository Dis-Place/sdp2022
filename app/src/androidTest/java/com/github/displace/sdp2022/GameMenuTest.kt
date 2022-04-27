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
        intent = Intent(ApplicationProvider.getApplicationContext(),GameVersusViewActivity::class.java)
    }

    @Test
    fun testPlayButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            onView(withId(R.id.TryText))
                .check(matches(withText("status : neutral, nombre d'essais restant : 4")))
        }
    }

    @Test
    fun testMap() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            onView(withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()))
        }

    }

    @Test
    fun testEndButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            Intents.init()
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
            Intents.release()
        }
    }

    @Test
    fun testFailButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            Intents.init()
            onView(withId(R.id.map)).perform(swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.TryText))
                .check(matches(withText("status : fail, nombre d'essais restant : 3 True : x=46.52048 y=6.56782")))
            Intents.release()
        }

    }

    @Test
    fun testWinButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            Intents.init()
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            Intents.intended(IntentMatchers.hasComponent(GameSummaryActivity::class.java.name))
            Intents.release()
        }
    }

    @Test
    fun testQuitButton() {
        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")

        ActivityScenario.launch<GameVersusViewActivity>(intent).use {
            Intents.init()
            onView(withId(R.id.closeButton)).perform(click())
            Intents.intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
            Intents.release()
        }
    }

  /*  @Test
    fun testChatButton() {

        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(null, false))
        Thread.sleep(3000)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app))
        Thread.sleep(1000)

        intent.putExtra("gid","-4862463398588582910")
        intent.putExtra("uid","hCkhhJ0dkINs0BIpx8eqhLWzXw43")
        intent.putExtra("nbPlayer",2)
        intent.putExtra("other","0")
        ActivityScenario.launch<GameSummaryActivity>(intent).use {

            onView(withId(R.id.chatButton)).perform(click())
            onView(withId(R.id.chatEditText)).perform(typeText("hh")).perform(closeSoftKeyboard())
            onView(withId(R.id.sendChatMessage)).perform(click())

        }
    }*/

    private fun swipeUp(): ViewAction? {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.TOP_CENTER, Press.FINGER
        )
    }

}
