package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.Invite
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable
import java.util.ArrayList


@RunWith(AndroidJUnit4::class)
class GameSummaryActivityTest {

    lateinit var bundle: Bundle
    lateinit var intent: Intent

    @Before
    fun createIntentAndBundle() {
        bundle = Bundle()
        intent = Intent(getApplicationContext(),GameSummaryActivity::class.java)
    }

    @Test
    fun victoryTextCorrectWhenWin() {
        bundle.putBoolean(EXTRA_RESULT, true)
        intent.putExtras(bundle)
        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.textViewResult)).check(matches(withText("VICTORY")))
        }
    }

    @Test
    fun victoryTextCorrectWhenLose() {
        bundle.putBoolean(EXTRA_RESULT, false)
        intent.putExtras(bundle)
        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.textViewResult)).check(matches(withText("DEFEAT")))
        }
    }

    @Test
    fun gameModeTextIsDisplayed() {
        val mode = "example_mode"
        bundle.putString(EXTRA_MODE, mode)
        intent.putExtras(bundle)
        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.textViewGameMode)).check(matches(withText(mode)))
        }
    }

    @Test
    fun friendInviteIsUsed() {

        val i1 = Invite(PartialUser("pipo","pipo"),PartialUser("pipo","pipo"))
        val i2 = Invite(PartialUser("pipo","pipo"),PartialUser("pipo","pipo"))
        val b = i1==i2
        val h = i1.hashCode()
        var p1 = i1.source
        p1 = i1.target

        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app,null, false))
        Thread.sleep(3000)
        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app))
        Thread.sleep(1000)

        val mode = "example_mode"
        val id = "dummy_id"
        bundle.putSerializable("others", listOf(listOf("",id)) as Serializable)
        bundle.putString(EXTRA_MODE, mode)
        intent.putExtras(bundle)
        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.friendInviteButton)).perform(click())
        }
    }

    @Test
    fun gameStatsCorrectlyDisplayed() {
        val statsList: ArrayList<String> = arrayListOf()

        statsList.add("1:23")
        statsList.add("4:56")
        statsList.add("7:08")
        statsList.add("9:10")
        statsList.add("11:12")

        bundle.putStringArrayList(EXTRA_STATS, statsList)
        intent.putExtras(bundle)

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.layoutGameStats)).check(matches(hasChildCount(6)))
            onView(withId(R.id.layoutGameStats)).check(matches(withChild(withText("1:23"))))
            onView(withId(R.id.layoutGameStats)).check(matches(withChild(withText("4:56"))))
            onView(withId(R.id.layoutGameStats)).check(matches(withChild(withText("7:08"))))
            onView(withId(R.id.layoutGameStats)).check(matches(withChild(withText("9:10"))))
            onView(withId(R.id.layoutGameStats)).check(matches(withChild(withText("11:12"))))
        }
    }

    @Test
    fun replayButtonWorks() {
        Intents.init()

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.gameListButton)).perform(click())
            intended(IntentMatchers.hasComponent(GameListActivity::class.java.name))
        }

        Intents.release()
    }

    @Test
    fun mainMenuButtonWorks() {
        Intents.init()


        val app = getApplicationContext() as MyApplication

        app.setActiveUser(CompleteUser(app, null, false))

        Thread.sleep(3_000)

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.mainMenuButton)).perform(click())
            intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))
        }
        Intents.release()
    }
}