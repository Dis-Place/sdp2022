package com.github.displace.sdp2022.profile.history

import kotlinx.serialization.Serializable

@Serializable
data class History(val map: String, val date: String, val result: String)        // add score and player list