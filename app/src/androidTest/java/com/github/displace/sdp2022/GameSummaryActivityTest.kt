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
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
    fun scoreDisplaysCorrectly() {
        bundle.putInt(EXTRA_SCORE_P1, 1)
        bundle.putInt(EXTRA_SCORE_P2, 2)
        intent.putExtras(bundle)
        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.scoreP1)).check(matches(withText("1")))
            onView(withId(R.id.scoreP2)).check(matches(withText("2")))
        }
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

        app.setActiveUser(CompleteUser(null))

        ActivityScenario.launch<GameSummaryActivity>(intent).use {
            onView(withId(R.id.mainMenuButton)).perform(click())
            intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))
        }
        Intents.release()
    }
}