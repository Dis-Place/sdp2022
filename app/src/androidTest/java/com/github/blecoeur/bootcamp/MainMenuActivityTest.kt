package com.github.blecoeur.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java).apply { putExtra(
            EXTRA_MESSAGE, "Baptou") }
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)
        try {
            Espresso.onView(ViewMatchers.withId(R.id.WelcomeText))
                .check(ViewAssertions.matches(ViewMatchers.withText("Welcome Baptou!")))
        }finally {
            scenario.close()
        }
    }
   /*
    @Test
    fun testPlayButton(){

    }
    @Test
    fun testProfileButton(){

    }
    @Test
    fun testButton(){

    }
    @Test
    fun testNewsButton(){

    }*/

}