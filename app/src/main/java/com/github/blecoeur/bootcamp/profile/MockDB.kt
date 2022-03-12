package com.github.blecoeur.bootcamp.profile

import com.github.blecoeur.bootcamp.profile.achievements.Achievement
import com.github.blecoeur.bootcamp.profile.friends.Friend
import com.github.blecoeur.bootcamp.profile.history.History
import com.github.blecoeur.bootcamp.profile.messages.Message
import com.github.blecoeur.bootcamp.profile.statistics.Statistic

class MockDB : ProfileDbConnection {
    private val dummyStatList : List<Statistic>  = listOf( Statistic("stat1",0)  ,
        Statistic("stat2",0) ,
        Statistic("stat3",0) )

    private val dummyAchList : List<Achievement>  = listOf( Achievement("ach1","today")  ,
        Achievement("ach1","today") ,
        Achievement("ach1","today") )

    private val dummyFriendList : List<Friend>  = listOf( Friend("friend1","1")  ,
        Friend("friend2","2") ,
        Friend("friend2","3") )

    private val dummyMessageList : List<Message>  = listOf( Message("MESSAGE 1 IT IS A VERY LONG MESSAGEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE","1", dummyFriendList[0])  ,
        Message("MESSAGE 2","2", dummyFriendList[1]) ,
        Message("MESSAGE 3","3",dummyFriendList[2]) )

    private val dummyHistList : List<History> = listOf( History("MAP","date1","RESULT") )


    override fun getAchList(size: Int, id: String): List<Achievement> {
   //     TODO("Not yet implemented")
        return dummyAchList.take(size)
    }

    override fun getHistList(size: Int, id: String): List<History> {
   //     TODO("Not yet implemented")
        return dummyHistList.take(size)
    }

    override fun getStatsList(size: Int, id: String): List<Statistic> {
//        TODO("Not yet implemented")
        return dummyStatList.take(size)
    }

    override fun getMsgList(size: Int, id: String): List<Message> {
    //    TODO("Not yet implemented")
        return dummyMessageList.take(size)
    }

    override fun getFriendsList(size: Int, id: String): List<Friend> {
       // TODO("Not yet implemented")
        return dummyFriendList.take(size)
    }

    override fun sendInvite(f :Friend) {
    //    TODO("Not yet implemented")
    }

    override fun sendMessage(msg : String , f : Friend) {
    //    TODO("Not yet implemented")
    }

    override fun getProfileInfo(id: String) : Friend {
    //    TODO("Not yet implemented")
        return Friend("friend0","0") //dummy Friend
    }
}