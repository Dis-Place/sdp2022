package com.github.displace.sdp2022.profile.history

import kotlinx.serialization.Serializable

@Serializable
data class History(val map: String, val date: String, val result: String)     {
    fun toMap(): Map<String, *> {
        return mapOf(Pair("map", map), Pair("date", date), Pair("result", result))
    }
}