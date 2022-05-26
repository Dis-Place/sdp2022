package com.github.displace.sdp2022.users

import kotlinx.serialization.Serializable

/**
 * Partial informations for the user, which are only the user id and the username
 * User more frequently and is sufficient to represent an online friend
 */
@Serializable
data class PartialUser(var username: String, val uid: String) { // profile picture later ?

    /**
     * Translates the partial user to a map
     * Adapts it to the database
     */
    fun toMap(): Map<String, *> {
        return mapOf(Pair("username", username), Pair("uid", uid))
    }

    override fun equals(other: Any?): Boolean {
        val otherUser = other as PartialUser?

        return otherUser?.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}