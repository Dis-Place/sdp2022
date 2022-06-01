package com.github.displace.sdp2022.profile.history

import kotlinx.serialization.Serializable

/**
 * Represents an entry in the Game History
 *
 * It contains a game mode, a date and a result ( victory or defeat ).
 */
@Serializable
data class History(val gameMode: String, val date: String, val result: String)     {
    fun toMap(): Map<String, *> {
        return mapOf(Pair("gameMode", gameMode), Pair("date", date), Pair("result", result))
    }
}