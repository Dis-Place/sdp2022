package com.github.blecoeur.bootcamp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchDummyUserTest {
    @get:Rule
    val testRule = ActivityScenarioRule(SearchDummyUser::class.java)
    val db =
        FirebaseDatabase.getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/")

    @Before
    fun setupDb() {
        db.getReference("users").child("John Cena").setValue(DummyUser("John Cena", 12))
    }

    @Test
    fun testWrongInput() {
        Intents.init()
        onView(ViewMatchers.withId(R.id.dummySearchName)).perform(ViewActions.replaceText("Jean Michel Rien Compris"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyGoButton)).perform(click())
        onView(ViewMatchers.withId(R.id.dummySearchAgeTextView)).check(matches(withText("Sorry, this user does not exist")))

        Intents.release()
    }

    @Test
    fun testGoodInput() {
        Intents.init()
        onView(ViewMatchers.withId(R.id.dummySearchName)).perform(ViewActions.replaceText("John Cena"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.dummyGoButton)).perform(click())
        onView(ViewMatchers.withId(R.id.dummySearchAgeTextView)).check(matches(withText("12 years old")))

        Intents.release()
    }


    @After
    fun cleanDb() {
        db.getReference("users").child("John Cena").removeValue()
    }
}