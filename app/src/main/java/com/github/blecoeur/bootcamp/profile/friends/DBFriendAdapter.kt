package com.github.blecoeur.bootcamp.profile.friends

import android.util.Log
import com.github.blecoeur.bootcamp.profile.statistics.Statistic

class DBFriendAdapter {

    private val dummyFriendList : List<Friend>  = listOf( Friend("friend1")  ,
        Friend("friend2") ,
        Friend("friend2") )

    fun getFriendList(size : Int) : List<Friend>{
        return dummyFriendList.take(size)
    }

    fun sendFriendMessage(friend : Friend) {
        Log.d("test","FRIEND MESSAGE")
    }

    fun sendFriendInvite(friend : Friend) {
        Log.d("test","FRIEND INVITE" )
    }

}