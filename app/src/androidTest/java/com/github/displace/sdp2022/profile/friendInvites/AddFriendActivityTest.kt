package com.github.displace.sdp2022.profile.friendInvites

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.profile.FriendRequest.Companion.sendFriendRequest
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class AddFriendActivityTest {

    lateinit var completeUser: CompleteUser

    val intent =
        Intent(ApplicationProvider.getApplicationContext(), AddFriendActivity::class.java).apply {
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


    @Test
    fun addButtonIsDisplayed() {


        val scenario = ActivityScenario.launch<AddFriendActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.sendFriendRequestButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

    }



    @Test
    fun editTextIsDisplayed() {

        val scenario = ActivityScenario.launch<AddFriendActivity>(intent)

        scenario.use {
            Espresso.onView(ViewMatchers.withId(R.id.friendRequestEditText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

    }

    @Test
    fun sendFriendRequestTest(){
        val partialUser = PartialUser("test","testUid")
        val target = "dummy"
        val mockContext = Mockito.mock(Context::class.java)
        val mockDatabaseReference = Mockito.mock(DatabaseReference::class.java)
        val mockInviteReference = Mockito.mock(DatabaseReference::class.java)
        val mockCompleteUserReference = Mockito.mock(DatabaseReference::class.java)
        `when`(mockDatabaseReference.child("Invites")).thenReturn(mockInviteReference)
        `when`(mockDatabaseReference.child("CompleteUsers")).thenReturn(mockCompleteUserReference)
        `when`(mockInviteReference.push()).thenReturn(mockInviteReference)
        sendFriendRequest(mockContext, target, mockDatabaseReference, partialUser)

    }




}