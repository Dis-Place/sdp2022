package com.github.displace.sdp2022.profile.friendInvites.utils

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.MockDatabaseUtils
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.profile.FriendRequest.Companion.sendFriendRequest
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing


//https://semaphoreci.com/community/tutorials/stubbing-and-mocking-with-mockito-2-and-junit
@RunWith(AndroidJUnit4::class)
class FriendRequestTest {

    lateinit var db : GoodDB

    val intent =
        Intent(ApplicationProvider.getApplicationContext(), FriendRequestTest::class.java).apply {
            putExtra("DEBUG", true)
        }

    @Before
    fun before(){

        MockDatabaseUtils.mockIntent(intent)
        db = DatabaseFactory.getDB(intent)
        val app = ApplicationProvider.getApplicationContext() as MyApplication
        DatabaseFactory.clearMockDB()

    }


    @Test
    fun checkUserExistsCheck(){
        val partialUsers : List<PartialUser> = listOf<PartialUser>(
            PartialUser("dude", "5"),  PartialUser("dudette", "4") )
        val target = "dudette"
        assertTrue(FriendRequest.checkUserExists(partialUsers,target))
    }

    @Test
    fun getPartialUsersTest(){

        val user1 = PartialUser("a", "1")
//        val user2 = PartialUser("b", "2")
        val data = mapOf("adsf" to mapOf("CompleteUser" to mapOf( "partialUser" to mapOf("username" to "a", "uid" to "1"))) )

        val result = FriendRequest.getPartialUsers(data)
        assertEquals(mutableListOf<PartialUser>(user1), result)
    }

    @Test
    fun getTargetTest(){
//        fun getTargetUser(users : List<PartialUser>, target : String) : PartialUser
        val target : PartialUser = PartialUser("testname", "testuid")
        val partialUsers : List<PartialUser> = listOf<PartialUser>(
            PartialUser("dude", "5"),  PartialUser("dudette", "4"),  PartialUser("testname", "testuid"))
        assertEquals(target ,  FriendRequest.getTargetUser(partialUsers, target.username))
    }

}
