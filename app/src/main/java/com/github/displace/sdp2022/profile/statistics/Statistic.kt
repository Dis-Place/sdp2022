package com.github.displace.sdp2022.profile.statistics

import kotlinx.serialization.Serializable

/**
 * Represents a statistic
 *
 * It contains a name and a value
 */
@Serializable
data class Statistic(@Serializable val name: String, @Serializable var value: Long)