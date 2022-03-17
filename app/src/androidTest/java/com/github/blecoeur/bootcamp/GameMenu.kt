package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameMenu {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */
    @Test
    fun testingInput() {
        val myPreferences = "myPrefs"

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)


        val context = getInstrumentation().targetContext
        val sharedpreferences = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        val editor = sharedpreferences.edit()
        editor.putString("userNameKey","Antoine")
        editor.commit()

        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        try {
            Espresso.onView(withId(R.id.WelcomeText))
                .check(ViewAssertions.matches(ViewMatchers.withText("Welcome Antoine!")))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testPlayButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("neutral")))
    }
    @Test
    fun testWinButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonWin)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("win")))
    }
    @Test
    fun testFailButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("fail")))
    }
    @Test
    fun testEndButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.triButtonFail)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("end of game")))
    }

    @Test
    fun testQuitButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.closeButton)).perform(click())
        Espresso.onView(withId(R.id.playVersusButton)).perform(click())
        Espresso.onView(withId(R.id.TryText)).check(matches(withText("neutral")))
    }
}