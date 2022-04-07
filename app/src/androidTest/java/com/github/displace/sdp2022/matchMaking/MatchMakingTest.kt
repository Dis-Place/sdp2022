package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.*
import com.github.displace.sdp2022.matchMaking.Lobby
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.news.NewsActivity
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchMakingTest {


    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setActiveUser(CompleteUser(null))

        Thread.sleep(3000)
    }

    @Test
    fun testPublicLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase
        Thread.sleep(1000)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000)

            db.delete("MM","")
            Espresso.onView(ViewMatchers.withId(R.id.textView6)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))
        }


        Thread.sleep(1000)

    }

    @Test
    fun testPublicLobbySearch(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase
        Thread.sleep(1000)


        scenario.use {

            //create mock lobby in DB
            var last : Long = 0
            //get lobby last : "join" that one
            db.referenceGet("MM/Versus/Map2/public","last").addOnSuccessListener { l ->
                if(l.value != null){
                    last = l.value as Long
                }
            }
            Thread.sleep(1000)

            db.update("MM/Versus/Map2/public","freeList",listOf("head","test"))
            db.update("MM/Versus/Map2/public/freeLobbies","test",Lobby("test", 3, PartialUser("FREE", "FREE")))

            Thread.sleep(1000)


            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive

            db.delete("MM","")

            Espresso.onView(ViewMatchers.withId(R.id.textView6)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)


        }
    }

    @Test
    fun testPublicLobbyGameLaunch(){
        Intents.init()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase

        Thread.sleep(1000)
        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive

            db.update("MM/Versus/Map2/public/freeLobbies/test","lobbyCount",2)
            Thread.sleep(1000)

            db.delete("MM","")
            Intents.intended(IntentMatchers.hasComponent(GameVersusViewActivity::class.java.name))

            Thread.sleep(1000)

        }
        Intents.release()
    }


    @Test
    fun testPrivateLobbyCreation(){
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase

        Thread.sleep(1000)

        scenario.use {

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("test")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyCreate)).perform(ViewActions.click())
            Thread.sleep(1000)
            db.delete("MM","")
            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)

        }
    }

    @Test
    fun testPrivateLobbySearch(){
        val intent = Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
            putExtra("DEBUG", true)
        }
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase
        Thread.sleep(1000)

        scenario.use {

            db.update("MM/Versus/Map2/private","freeList",listOf("head","test"))
            db.update("MM/Versus/Map2/private/freeLobbies","test",Lobby("test", 3, PartialUser("FREE", "FREE")))

            Thread.sleep(1000)

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("test")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyJoin)).perform(ViewActions.click())
            Thread.sleep(1000)

            db.delete("MM","")


            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)

        }
    }

}