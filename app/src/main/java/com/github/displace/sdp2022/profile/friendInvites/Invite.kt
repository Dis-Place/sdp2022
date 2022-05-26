package com.github.displace.sdp2022.profile.friendInvites

import com.github.displace.sdp2022.users.PartialUser

import kotlinx.serialization.Serializable


/**
 * Data Class representing an invite
 * @param source : user that sent the invite
 * @param target : user that the invite is sent to
 */
@Serializable
data class Invite(val source: PartialUser, val target : PartialUser){
    override fun equals(other: Any?): Boolean {
        val otherInvite = other as Invite?

        return otherInvite?.source == source && otherInvite?.target == target
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}

/**
 * Data Class that contains an Invite and its Id to be able to access it in the database
 * No need for serialization as it is only used locally
 * @param invite : invite
 * @param id : id of the invite in the database
 */
data class InviteWithId(val invite: Invite, val id : String)

