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
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.friends.Friend
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchMakingTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)
/*
    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

    }*/


    @Test
    fun testPublicLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

    @Test
    fun testPublicLobbySearch(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

    @Test
    fun testPrivateLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {  }
    }

    @Test
    fun testPrivateLobbySearch(){
           val intent =
               Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
           val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

           scenario.use {  }
    }

}