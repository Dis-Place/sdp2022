package com.github.displace.sdp2022.profile

import com.github.blecoeur.bootcamp.profile.achievements.Achievement
import com.github.blecoeur.bootcamp.profile.friends.Friend
import com.github.blecoeur.bootcamp.profile.history.History
import com.github.blecoeur.bootcamp.profile.messages.Message
import com.github.blecoeur.bootcamp.profile.statistics.Statistic

interface ProfileDbConnection {

    fun getAchList(size : Int, id : String) : List<Achievement>

    fun getHistList(size : Int, id : String) : List<History>

    fun getStatsList(size : Int, id : String) : List<Statistic>

    fun getMsgList(size : Int, id : String) : List<Message>

    fun getFriendsList(size : Int, id : String) : List<Friend>
    fun sendInvite(f : Friend)
    fun sendMessage(msg : String ,id : String)

    fun getProfileInfo( id : String) : Friend

    fun getActiveUser() : Friend

}