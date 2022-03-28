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
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

 //   @get:Rule
 //   val testRule = ActivityScenarioRule(ProfileActivity::class.java)

    @Before
    fun before(){
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

    }


    @Test
    fun testInboxButton() {

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.InboxScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testMessageInInboxButton() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

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
            Espresso.onView(ViewMatchers.withId(R.id.sendButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText("Baptou")
                )
            )
        }
    }

    @Test
    fun testProfileButton() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

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
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.FriendsScroll))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testSettingsButton() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.editProfile))
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun testMessageInFriendsButton() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)
        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.recyclerFriend)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MsgViewHolder>(
                    0,
                    clickInInnerObject(R.id.messageButton)
                )
            )
            Espresso.onView(ViewMatchers.withId(R.id.sendButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.profileUsername)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText("Baptou")
                )
            )
        }
    }

    @Test
    fun testFriendProfile() {
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        app.setDb(MockDB())
        app.setActiveUser(Friend("Baptou", "0"))

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