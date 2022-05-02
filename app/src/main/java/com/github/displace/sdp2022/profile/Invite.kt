package com.github.displace.sdp2022.profile

import com.github.displace.sdp2022.users.PartialUser

import kotlinx.serialization.Serializable

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