package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.*
import com.github.displace.sdp2022.GameMenuTest.Companion.MOCK_GPS_POSITION
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.matchMaking.Lobby
import com.github.displace.sdp2022.matchMaking.MatchMakingActivity
import com.github.displace.sdp2022.profile.TestingUtils
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.messages.MsgViewHolder
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.gps.MockGPS
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

@RunWith(AndroidJUnit4::class)
class MatchMakingTest {

    lateinit var intent : Intent

    lateinit var db : GoodDB

    @Before
    fun before(){
        intent =
        Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
            putExtra("DEBUG", true)
        }
        DatabaseFactory.clearMockDB()
        MockDatabaseUtils.mockIntent(intent)
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        DatabaseFactory.clearMockDB()
        app.setActiveUser(CompleteUser(app,null, DatabaseFactory.MOCK_DB))

        app.setMessageHandler(MessageHandler(app.getActiveUser()!!.getPartialUser(),app,intent))
        Thread.sleep(1000)

        db = DatabaseFactory.getDB(intent)

        db.update("MM/Versus/Map2/private/freeList", listOf("head"))
        db.update(
            "MM/Versus/Map2/private/freeLobbies/freeHead",
            Lobby("freeHead", 0, PartialUser("FREE", "FREE") , GeoPoint(0.0,0.0)).toMap()
        )
        db.update(
            "MM/Versus/Map2/private/launchLobbies/launchHead",
            Lobby("launchHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0)).toMap()
        )
        db.update("MM/Versus/Map2/public/freeList", listOf("head"))
        db.update(
            "MM/Versus/Map2/public/freeLobbies/freeHead",
            Lobby("freeHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0)).toMap()
        )
        db.update(
            "MM/Versus/Map2/public/launchLobbies/launchHead",
            Lobby("launchHead", 0, PartialUser("FREE", "FREE"),GeoPoint(0.0,0.0)).toMap()
        )

        Thread.sleep(3000)

    }

    /*@After
    fun releaseIntents() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.getActiveUser()?.removeUserFromDatabase()
    }*/

    @Test
    fun testPublicLobbyCreation(){
   /*     val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }*/
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

      //  val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase

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
      /*  val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }*/
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

     //   val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase
        Thread.sleep(1000)


        scenario.use {
            MockGPS.specifyMock(intent, MOCK_GPS_POSITION)

            db.update("MM/Versus/Map2/public/freeList",listOf("head","test"))
            db.update("MM/Versus/Map2/public/freeLobbies/test",
                Lobby("test", 3, PartialUser("FREE", "FREE"), GeoPoint(0.00001,0.00001)).toMap()
            )

            Thread.sleep(1000)


            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive


            Espresso.onView(ViewMatchers.withId(R.id.textView6)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)


        }
    }

    @Test
    fun testPublicLobbyGameLaunch(){
        Intents.init()
     /*   val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }*/
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

    //    val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase

        Thread.sleep(1000)
        scenario.use {
            MockGPS.specifyMock(intent, MOCK_GPS_POSITION)

            Espresso.onView(ViewMatchers.withId(R.id.RandomLobbySearch)).perform(ViewActions.click())
            Thread.sleep(1000) //wait for the info to arrive

            db.getThenCall<ArrayList<String>>("MM/Versus/Map2/public/freeList"){ lobbies ->
                val lobby = lobbies?.get(1)
                db.update("MM/Versus/Map2/public/freeLobbies/$lobby/lobbyCount",2L)
            }


            Thread.sleep(1000)
            Intents.intended(IntentMatchers.hasComponent(GameVersusViewActivity::class.java.name))



        }
        Intents.release()
    }


    @Test
    fun testPrivateLobbyCreation(){
        /*val intent =
            Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
                putExtra("DEBUG", true)
            }*/
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

   //     val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase

        Thread.sleep(1000)

        scenario.use {
            MockGPS.specifyMock(intent, MOCK_GPS_POSITION)

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("test")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Thread.sleep(1000)

            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyCreate)).perform(ViewActions.click())
            Thread.sleep(1000)

            Espresso.onView(ViewMatchers.withId(R.id.friendsMMRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    TestingUtils().clickInInnerObject(R.id.inviteButton)
                )
            )

            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)

        }
    }

    @Test
    fun testPrivateLobbySearch(){
     /*   val intent = Intent(ApplicationProvider.getApplicationContext(), MatchMakingActivity::class.java).apply {
            putExtra("DEBUG", true)
        }*/
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)

   //     val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",true) as RealTimeDatabase
        Thread.sleep(1000)

        scenario.use {
            MockGPS.specifyMock(intent, MOCK_GPS_POSITION)

            db.update("MM/Versus/Map2/private/freeList",listOf("head","test"))
            db.update("MM/Versus/Map2/private/freeLobbies/test",
                Lobby("test", 3, PartialUser("FREE", "FREE"),GeoPoint(0.00001,0.00001)).toMap()
            )

            Thread.sleep(1000)

            Espresso.onView(ViewMatchers.withId(R.id.lobbyIdInsert))
                .perform(ViewActions.replaceText("test")).perform(
                    ViewActions.closeSoftKeyboard()
                )
            Espresso.onView(ViewMatchers.withId(R.id.privateLobbyJoin)).perform(ViewActions.click())
            Thread.sleep(1000)


            Espresso.onView(ViewMatchers.withId(R.id.textView14)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))

            Thread.sleep(1000)

        }
    }

    @Test
    fun testGameListToMM(){
        val intent = Intent(ApplicationProvider.getApplicationContext(), GameListActivity::class.java)
        val scenario = ActivityScenario.launch<MatchMakingActivity>(intent)


        scenario.use {
            MockGPS.specifyMock(intent, MOCK_GPS_POSITION)

            Espresso.onView(ViewMatchers.withId(R.id.playVersusButton)).perform(ViewActions.click())
        }
    }

}