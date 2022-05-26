package com.github.displace.sdp2022.profile.friendInvites.utils

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.profile.FriendRequest.Companion.sendFriendRequest
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing


//https://semaphoreci.com/community/tutorials/stubbing-and-mocking-with-mockito-2-and-junit
@RunWith(AndroidJUnit4::class)
class FriendRequestTest {


    @Test
    fun checkUserExistsCheck(){
        val partialUsers : List<PartialUser> = listOf<PartialUser>(
            PartialUser("dude", "5"),  PartialUser("dudette", "4") )
        val target = "dudette"
        assertTrue(FriendRequest.checkUserExists(partialUsers,target))
    }

    @Test
    fun getPartialUsersTest(){
        val mockDataSnapshot = Mockito.mock(DataSnapshot::class.java)
        val result = FriendRequest.getPartialUsers(mockDataSnapshot)
        assertEquals(mutableListOf<PartialUser>(), result)
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
