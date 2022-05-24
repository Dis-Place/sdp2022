package com.github.displace.sdp2022.users

import kotlinx.serialization.Serializable

@Serializable
data class PartialUser(var username: String, val uid: String) { // profile picture later ?
    override fun equals(other: Any?): Boolean {
        val otherUser = other as PartialUser?

        return otherUser?.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    fun toMap(): Map<String, *> {
        return mapOf(Pair("username", username), Pair("uid", uid))
    }
}