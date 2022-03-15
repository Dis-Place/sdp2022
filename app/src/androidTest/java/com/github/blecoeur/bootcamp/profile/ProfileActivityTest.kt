package com.github.blecoeur.bootcamp.profile

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.blecoeur.bootcamp.MainActivity
import com.github.blecoeur.bootcamp.MyApplication
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.friends.Friend
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun startToProfileTest(){
        Espresso.onView(ViewMatchers.withId(R.id.mainGoButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profileButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profileUsername)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Name")
            )
        )
    }

    @Test
    fun testInboxButton(){
        val app = ApplicationProvider.getApplicationContext()  as MyApplication
        app.setDb( MockDB() )
        app.setActiveUser(Friend("Baptou","0"))

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        try {
            Espresso.onView(ViewMatchers.withId(R.id.inboxButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.InboxScroll)).check(ViewAssertions.matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testProfileButton(){
        val app = ApplicationProvider.getApplicationContext()  as MyApplication
        app.setDb( MockDB() )
        app.setActiveUser(Friend("Baptou","0"))

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        try {
            Espresso.onView(ViewMatchers.withId(R.id.innerProfileButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.ProfileScroll)).check(ViewAssertions.matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testFriendsButton(){
        val app = ApplicationProvider.getApplicationContext()  as MyApplication
        app.setDb( MockDB() )
        app.setActiveUser(Friend("Baptou","0"))

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        try {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.FriendsScroll)).check(ViewAssertions.matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testSettingsButton(){
        val app = ApplicationProvider.getApplicationContext()  as MyApplication
        app.setDb( MockDB() )
        app.setActiveUser(Friend("Baptou","0"))

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        try {
            Espresso.onView(ViewMatchers.withId(R.id.profileSettingsButton)).perform(click())
            Espresso.onView(ViewMatchers.withId(R.id.textView3)).check(ViewAssertions.matches(isDisplayed()))
        }finally {
            scenario.close()
        }
    }

    @Test
    fun testMessageInInboxButton(){

    }

    @Test
    fun testMessageInFriendsButton(){

    }

/*    @Test
    fun testFriendProfile(){
        val app = ApplicationProvider.getApplicationContext()  as MyApplication
        app.setDb( MockDB() )
        app.setActiveUser(Friend("Baptou","0"))

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        try {
            Espresso.onView(ViewMatchers.withId(R.id.friendsButton)).perform(click())
        }finally {
            scenario.close()
        }
    }*/

}