package com.github.displace.sdp2022.profile.friendInvites

import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4::class)
class FriendRequestViewAdapterTest {

    lateinit var friendRequestViewAdapter: FriendRequestViewAdapter

    @Before
    fun before() {
        var dataset = mutableListOf<InviteWithId>(
            InviteWithId(Invite(PartialUser("a", "1"), PartialUser("b", "2")), "a1b2"),
            InviteWithId(Invite(PartialUser("c", "3"), PartialUser("d", "4")), "c3d4")
        )

        friendRequestViewAdapter = FriendRequestViewAdapter(dataset)
        Thread.sleep(100)
    }


    @Test
    fun getItemCountTest(){
        assertEquals( 2 , friendRequestViewAdapter.itemCount)
    }

    @Test
    fun deleteItemTest(){
        val unused = friendRequestViewAdapter.deleteRequest(1)
        assertEquals(1, friendRequestViewAdapter.itemCount)
    }

    @Test
    fun deleteCorrectItemTest(){
        val expected = InviteWithId(Invite(PartialUser("c", "3"), PartialUser("d", "4")), "c3d4")
        val result = friendRequestViewAdapter.deleteRequest(1)
        assertEquals(expected, result)
    }


    @Test
    fun frienRequestViewAdapterTest(){

    }

}


