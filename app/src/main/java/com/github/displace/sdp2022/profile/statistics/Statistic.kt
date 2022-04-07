package com.github.displace.sdp2022.profile.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Statistic(@Serializable val name: String, @Serializable var value: Long)