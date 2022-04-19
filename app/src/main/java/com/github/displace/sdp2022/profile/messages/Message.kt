package com.github.displace.sdp2022.profile.messages

import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.users.PartialUser

class Message(val message: String, val date: String, val sender: PartialUser){
    override fun equals(other: Any?): Boolean {
        val otherUser = other as Message?

        return otherUser?.message == message && otherUser?.date == date && otherUser?.sender == sender
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}