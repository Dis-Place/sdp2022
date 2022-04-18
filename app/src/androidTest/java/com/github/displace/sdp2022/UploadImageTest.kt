package com.github.displace.sdp2022

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UploadImageTest {
    @get:Rule
    val testRule = ActivityScenarioRule(UploadImageActivity::class.java)

    /*
    @Test
    fun canSearchImage() {
        Intents.init()
        Espresso.onView(withId(R.id.imageUpload)).perform(click())
        intended(IntentMatchers.anyIntent())
        Intents.release()
    }

    @Test
    fun canUploadImage() {
        Intents.init()

        Espresso.onView(withId(R.id.uploadButton)).perform(click())
        intended(IntentMatchers.hasComponent(MainMenuActivity::class.java.name))

        Intents.release()
    }
     */
}