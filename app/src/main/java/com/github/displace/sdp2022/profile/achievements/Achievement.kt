package com.github.displace.sdp2022.profile.achievements

import kotlinx.serialization.Serializable

/**
 * Represents an Achievement
 *
 * It contains a name, a description and a date.
 */
@Serializable
data class Achievement(val name: String , val description : String = "" ,val date: String) // date is in DD-MM-YYYY