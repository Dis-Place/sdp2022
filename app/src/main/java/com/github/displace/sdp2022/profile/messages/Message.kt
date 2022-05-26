package com.github.displace.sdp2022.profile.messages

import com.github.displace.sdp2022.users.PartialUser
import kotlinx.serialization.Serializable

/**
 * Represents a Message
 *
 * It contains the content of the message, a date and the sender of the message.
 */
@Serializable
class Message(val message: String, val date: String, val sender: PartialUser){
    /**
     * Checks if two messages are equal
     * @param other : the other message to check against
     */
    override fun equals(other: Any?): Boolean {
        val otherUser = other as Message?

        return otherUser?.message == message && otherUser.date == date && otherUser.sender == sender
    }

    /**
     * Returns the hashcode of the message
     * @return the hashcode of the message
     */
    override fun hashCode(): Int {
        return message.hashCode()
    }

    fun toMap(): Map<String, *> {
        return mapOf(Pair("message", message), Pair("date", date), Pair("sender", sender.toMap()))
    }
}