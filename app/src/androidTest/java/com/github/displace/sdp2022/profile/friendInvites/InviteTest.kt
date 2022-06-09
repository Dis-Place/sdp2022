package com.github.displace.sdp2022.profile.friendInvites

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.users.PartialUser
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InviteTest {
    private val user1 = PartialUser("name","uid")
    private val user2 = PartialUser("name","uid")

    @Test
    fun testHashCode(){
        assertTrue(user1.hashCode() == user2.hashCode())
    }

    @Test
    fun testEquals(){
        assertTrue(user1 == user2)
    }

    @Test
    fun testEqualsFailsOnDifferentUsers(){
        val user3 = PartialUser("uid", "name")
        assertFalse(user1 == user3)
    }

    @Test
    fun toMap(){
        val invite = Invite(user1, user2)
        val mapped = invite.toMap()
        val expected = mapOf("source" to user1.toMap(), "target" to user2.toMap())
        assertEquals(expected, mapped)

    }

}