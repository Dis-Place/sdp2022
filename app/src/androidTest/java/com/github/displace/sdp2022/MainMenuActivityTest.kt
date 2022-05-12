package com.github.displace.sdp2022

import android.content.Intent
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.displace.sdp2022.news.NewsActivity
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.qrcode.QrCodeTemp
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuActivityTest {

    @Before
    fun before() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app, null))

        Thread.sleep(3000)
    }

    @After
    fun after() {
        (ApplicationProvider.getApplicationContext() as MyApplication).getActiveUser()
            ?.removeUserFromDatabase()
    }

    /*
     Test if the input of the main screen is correctly shown in the main menu
     */
    @Test
    fun testingInput() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.WelcomeText))
                .check(matches(withText("Welcome defaultName!")))
        }
    }

    @Test
    fun testProfileButton() {
        Intents.init()

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.profileButton)).perform(click())
        }

        intended(hasComponent(ProfileActivity::class.java.name))
        Intents.release()
    }

//    @Test
//    fun testSettingsButton() {
//        Intents.init()
//
//        val intent =
//            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
//        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)
//
//        scenario.use {
//            Espresso.onView(withId(R.id.settingsButton)).perform(click())
//        }
//
//        intended(hasComponent(SettingsActivity::class.java.name))
//        Intents.release()
//    }

    @Test
    fun testNewsButton() {
        Intents.init()

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.newsButton)).perform(click())
        }

        intended(hasComponent(NewsActivity::class.java.name))
        Intents.release()

    }

    @Test
    fun testPlayButton() {
        Intents.init()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.playButton)).perform(click())
        }

        intended(hasComponent(GameListActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun playButtonDontWorkWhenOffline() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(app, null, offlineMode = true))
        Intents.init()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.playButton)).perform(click())
            Espresso.onView(withId(R.id.titleText))
                .check(matches(ViewMatchers.isDisplayed()))
        }

        Intents.release()
    }

    @Test
    fun testDemoButton() {
        Intents.init()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.qrCodeDemo)).perform(click())
        }

        intended(hasComponent(QrCodeTemp::class.java.name))
        Intents.release()
    }

    @Test
    fun mapButtonGoesToDemoMapActivity() {
        getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant " + getTargetContext().packageName
                    + " android.permission.ACCESS_FINE_LOCATION"
        );
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity::class.java)
        val scenario = ActivityScenario.launch<MainMenuActivity>(intent)

        scenario.use {
            Espresso.onView(withId(R.id.mapButton)).perform(click())
            Espresso.onView(withId(R.id.map))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

}