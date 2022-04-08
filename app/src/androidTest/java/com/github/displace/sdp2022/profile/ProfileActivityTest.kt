package com.github.displace.sdp2022.profile

import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.profile.friends.FriendViewHolder
import com.github.displace.sdp2022.profile.messages.MsgViewHolder
import com.github.displace.sdp2022.users.CompleteUser
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    lateinit var completeUser: CompleteUser

    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        completeUser = CompleteUser(null, false)
        app.setActiveUser(completeUser)
    }

    @After
    fun after() {
        completeUser.removeUserFromDatabase()
    }


    @Test
    fun testInboxButton() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Thread.sleep(30)
            Espresso.onView(ViewMatchers.withId(R.id.InboxScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testMessageInInboxButton() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
        scenario.use { _ ->
            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.recyclerMsg)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    clickInInnerObject(R.id.replyButton)
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

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.innerProfileButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.ProfileScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testFriendsButton() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
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

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Thread.sleep(30)
            Espresso.onView(ViewMatchers.withId(R.id.editProfile))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testMessageInFriendsButton() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Thread.sleep(50)
            Espresso.onView(ViewMatchers.withId(R.id.recyclerFriend)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    clickInInnerObject(R.id.messageButton)
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
    fun testFriendProfile() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.recyclerFriend))
                .perform(RecyclerViewActions.actionOnItemAtPosition<FriendViewHolder>(0, click()))
            Espresso.onView(ViewMatchers.withId(R.id.friendUsername))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }


    private fun clickInInnerObject(id: Int): ViewAction {
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