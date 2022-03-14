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
class MainMenuActivityTest {
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
        editor.putString("userNameKey","Baptou")
        editor.commit()

        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        try {
            Espresso.onView(withId(R.id.WelcomeText))
                .check(ViewAssertions.matches(ViewMatchers.withText("Welcome Baptou!")))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testPlayButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.playButton)).perform(click())
        Espresso.onView(withId(R.id.textView2)).check(matches(withText("PLAY")))
    }
    @Test
    fun testProfileButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.profileButton)).perform(click())
        Espresso.onView(withId(R.id.profileUsername)).check(matches(withText("PROFILE")))
    }
    @Test
    fun testSettingsButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.settingsButton)).perform(click())
        Espresso.onView(withId(R.id.textView4)).check(matches(withText("SETTINGS")))
    }
    @Test
    fun testNewsButton(){
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.newsButton)).perform(click())
        Espresso.onView(withId(R.id.textView)).check(matches(withText("NEWS")))
    }
    @Test
    fun leaveAndComeBack(){ //how to test the upward navigation???
        Espresso.onView(withId(R.id.mainGoButton)).perform(click())
        Espresso.onView(withId(R.id.newsButton)).perform(click())
        Espresso.onView(withId(R.id.textView)).check(matches(withText("NEWS")))
    }
}