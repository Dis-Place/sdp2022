package com.github.displace.sdp2022.profile.achievements

import kotlinx.serialization.Serializable

/*
Make the date a Date or a String
 */
@Serializable
data class Achievement(val name: String ,val date: String) // date is in DD-MM-YYYY