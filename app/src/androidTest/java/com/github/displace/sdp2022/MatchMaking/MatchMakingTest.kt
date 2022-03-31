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
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.users.CompleteUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchMakingTest {


    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(CompleteUser(null))

    }

    @Test
    fun testPublicLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000)
            Espresso.onView(ViewMatchers.withId(R.id.textView6)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))
        }

        Thread.sleep(1000)

    }

    @Test
    fun testPublicLobbySearch(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
        val ls : List<Long> = listOf(0)
        Thread.sleep(1000)


        scenario.use {

            //create mock lobby in DB
            var last : Long = 0
            //get lobby last : "join" that one
            db.referenceGet("MM/Versus/Map1/public","last").addOnSuccessListener { l ->
                if(l.value != null){
                    last = l.value as Long
                }
            }
            Thread.sleep(1000)

            db.update("MM/Versus/Map1/public/L_$last","p_count",1)
            db.update("MM/Versus/Map1/public/L_$last","max_p",2)
            db.update("MM/Versus/Map1/public/L_$last","p_list",ls)
            db.update("MM/Versus/Map1/public/L_$last","launch",false)
            db.insert("MM/Versus/Map1/public/L_$last", "leader", 0)

            Thread.sleep(1000)


            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive

            db.delete("MM/Versus/Map1/public","L_$last")

            Espresso.onView(ViewMatchers.withId(R.id.textView6)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)


        }
    }

    @Test
    fun testPublicLobbyGameLaunch(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase

        Thread.sleep(1000)
        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive

            var last : Long = 0
            //get lobby last : "join" that one
            db.referenceGet("MM/Versus/Map1/public","last").addOnSuccessListener { l ->
                if(l.value != null){
                    last = l.value as Long
                }
            }
            Thread.sleep(1000)

            db.update("MM/Versus/Map1/public/L_$last","p_count",2)
            Thread.sleep(1000)


            db.delete("MM/Versus/Map1/public","L_$last")

            Thread.sleep(1000)

        }
    }

    @Test
    fun testPrivateLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase

        Thread.sleep(1000)

        scenario.use {

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("baptouGaming3")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyCreate)).perform(ViewActions.click())
            Thread.sleep(1000)
            db.delete("MM/Versus/Map1/public","L_baptouGaming3")
            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))


            Thread.sleep(1000)

        }
    }

    @Test
    fun testPrivateLobbySearch(){
        val intent = Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
        val ls : List<Long> = listOf(0)
        Thread.sleep(1000)

        scenario.use {

            db.update("MM/Versus/Map1/private/L_baptouGaming2","p_count",1)
            db.update("MM/Versus/Map1/private/L_baptouGaming2","max_p",2)
            db.update("MM/Versus/Map1/private/L_baptouGaming2","p_list",ls)
            db.update("MM/Versus/Map1/private/L_baptouGaming2","launch",false)
            db.insert("MM/Versus/Map1/private/L_baptouGaming2", "leader", 100)

            Thread.sleep(1000)

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("baptouGaming2")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyJoin)).perform(ViewActions.click())
            Thread.sleep(1000)

            db.delete("MM/Versus/Map1/private","L_baptouGaming2")

            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)

        }
    }

}