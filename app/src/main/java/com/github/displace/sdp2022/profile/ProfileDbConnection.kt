package com.github.displace.sdp2022.profile

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.PartialUser

interface ProfileDbConnection {

    fun getAchList(size: Int, id: String): List<Achievement>

    fun getHistList(size: Int, id: String): List<History>

    fun getStatsList(size: Int, id: String): List<Statistic>

    fun getMsgList(size: Int, id: String): List<Message>

    fun getFriendsList(size: Int, id: String): List<PartialUser>
    fun sendInvite(f: PartialUser)
    fun sendMessage(msg: String, id: String)

    fun getProfileInfo(id: String): Friend

    fun getActiveUser(): Friend

}