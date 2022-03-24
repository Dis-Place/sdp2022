package com.github.displace.sdp2022.users

class PartialUser(val username: String, val uid: String) { // profile picture later ?

    override fun equals(other: Any?): Boolean {
        val otherUser = other as PartialUser
        return otherUser.uid == uid
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}