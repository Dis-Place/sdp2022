package com.github.displace.sdp2022.MatchMaking

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.profile.ProfileActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchMakingTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MatchMakingActivity::class.java)


    @Test
    fun testLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

    @Test
    fun testLobbySearch(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

    @Test
    fun testSecondLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

}