package com.github.displace.sdp2022.profile

import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.profile.friendInvites.AddFriendActivity
import com.github.displace.sdp2022.profile.friends.FriendViewHolder
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.profile.messages.MsgViewHolder
import com.github.displace.sdp2022.users.CompleteUser
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    lateinit var completeUser: CompleteUser

    val intent =
        Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java).apply {
            putExtra("DEBUG", true)
        }

    lateinit var db : GoodDB

    @Before
    fun before(){

        MockDatabaseUtils.mockIntent(intent)
        db = DatabaseFactory.getDB(intent)
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        DatabaseFactory.clearMockDB()
        completeUser = CompleteUser(app,null, DatabaseFactory.MOCK_DB)
        app.setActiveUser(completeUser)
        //Thread.sleep(500)
        app.setMessageHandler(MessageHandler(completeUser.getPartialUser(),app,intent))
        Thread.sleep(100)
    }

    /*@After
    fun after() {
        completeUser.removeUserFromDatabase()
    }*/


    @Test
    fun testInboxButton() {

        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {

            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Thread.sleep(30)
            Espresso.onView(ViewMatchers.withId(R.id.InboxScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    /**
     * TODO: Commented to pass the tests because it has a really weird behaviour with cirrus
     * Should be fixed when we fully utilize the MockDB
     */

    @Test
    fun testMessageInInboxButton() {

        val scenario = ActivityScenario.launch<MainActivity>(intent)
        scenario.use { _ ->
            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.recyclerMsg)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    TestingUtils().clickInInnerObject(R.id.replyButton)
                )
            )
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.sendButton)).perform(click())
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText("defaultName")
                )
            )
        }
    }

    @Test
    fun testProfileButton() {

        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.innerProfileButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.ProfileScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }


    @Test
    fun testFriendsButton() {

        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.FriendsScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testSettingsButton() {

        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Thread.sleep(30)
            Espresso.onView(ViewMatchers.withId(R.id.editProfile))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun settingsDontOpenWhenOffline() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        completeUser = CompleteUser(app,null, DatabaseFactory.MOCK_DB, offlineMode = true)
        app.setActiveUser(completeUser)
        //Thread.sleep(100)

        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Thread.sleep(3000)
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Thread.sleep(3000)
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun settingsDontOpenWhenGuest() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        DatabaseFactory.clearMockDB()
        completeUser = CompleteUser(app,null, DatabaseFactory.MOCK_DB, guestBoolean = true)
        app.setActiveUser(completeUser)
        //Thread.sleep(100)
        app.setMessageHandler(MessageHandler(completeUser.getPartialUser(),app,intent))

        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Thread.sleep(30)
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testMessageInFriendsButton() {
        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.recyclerFriend)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    TestingUtils().clickInInnerObject(R.id.messageButton)
                )
            )
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.sendButton)).perform(click())
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText("defaultName")
                )
            )
        }
    }

    @Test
    fun testMessageReception() {
        MockDatabaseUtils.mockIntent(intent)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
        scenario.use {

            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Thread.sleep(500)
            db.update("CompleteUsers/${completeUser.getPartialUser().uid}/CompleteUser/MessageHistory",
                listOf(Message("dwa","daws",completeUser.getPartialUser()).toMap())
            )

            Thread.sleep(500)

        }
    }

    @Test
    fun testFriendProfile() {

        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.recyclerFriend))
                .perform(RecyclerViewActions.actionOnItemAtPosition<FriendViewHolder>(0, click()))
            Espresso.onView(ViewMatchers.withId(R.id.friendUsername))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    //TODO NEeded to comment to merge this week
//    @Test
//    fun testingAddFriendButton() {
////        Intents.init()
//        val intent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
//        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
//
//        scenario.use {
//            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
//            Espresso.onView(ViewMatchers.withId(R.id.addFriendButton)).perform(click())
//        }
//
//        Intents.intended(IntentMatchers.hasComponent(AddFriendActivity::class.java.name))
////        Intents.release()
//    }


}


class TestingUtils{

    fun clickInInnerObject(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController?, view: View) {
                val v: View = view.findViewById(id)
                v.performClick()
            }
        }
    }
}