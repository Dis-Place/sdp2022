package com.github.displace.sdp2022.profile.friendInvites.utils

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.ReceiveFriendRequests
import com.github.displace.sdp2022.profile.ReceiveFriendRequests.Companion.getUserInvites
import com.github.displace.sdp2022.profile.friendInvites.Invite
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@RunWith(AndroidJUnit4::class)
class ReceiveFriendRequestTest {


    @Test
    fun receiveRequestsTest(){
        val mockDatabaseReference = Mockito.mock(DatabaseReference::class.java)
        val mockInviteReference = Mockito.mock(DatabaseReference::class.java)
        `when`(mockDatabaseReference.child("Invites")).thenReturn(mockInviteReference)


        val partialUser = PartialUser("dummy", "1'")
        val result = ReceiveFriendRequests.receiveRequests(mockDatabaseReference, partialUser)

        var data : List<InviteWithId>? = listOf<InviteWithId>()
     //   assertEquals( data , result.value)
    }


    @Test
    fun getUserInvitesTest() {
        val userUid = "asdf"
        val invites = mutableListOf<InviteWithId>(
            InviteWithId(Invite(PartialUser("a", "test"), (PartialUser("b", "asdf"))), "5"),
            InviteWithId(Invite(PartialUser("c", "test"), (PartialUser("d", "fdsdf"))), "4")
        )
        val expected = mutableListOf<InviteWithId>(
            InviteWithId(Invite(PartialUser("a", "test"), (PartialUser("b", "asdf"))), "5")
        )
        assertEquals(expected, getUserInvites(invites, userUid))
    }

    @Test
    fun getInvitesTest(){
        val u1 = PartialUser("a", "1")
        val u2 = PartialUser("b", "2")
        val data = mapOf( "asdf" to mapOf("source" to u1.toMap(), "target" to u2.toMap()))

        val result = ReceiveFriendRequests.getInvites(data)
        assertEquals(mutableListOf<InviteWithId>(  InviteWithId(Invite(u1,u2), "asdf")), result)
    }

}
