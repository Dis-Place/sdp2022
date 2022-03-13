package com.github.blecoeur.bootcamp.profile

import com.github.blecoeur.bootcamp.profile.achievements.Achievement
import com.github.blecoeur.bootcamp.profile.friends.Friend
import com.github.blecoeur.bootcamp.profile.history.History
import com.github.blecoeur.bootcamp.profile.messages.Message
import com.github.blecoeur.bootcamp.profile.statistics.Statistic

class MockDB : ProfileDbConnection {

    var id : Int = 0;

    private var dummyStatList : List<Statistic>  = listOf( Statistic("stat1",0)  ,
        Statistic("stat2",0) ,
        Statistic("stat3",0) )

    private var dummyAchList : List<Achievement>  = listOf( Achievement("ach1","today")  ,
        Achievement("ach1","today") ,
        Achievement("ach1","today") )

    private var dummyFriendList : List<Friend>  = listOf( Friend("friend1","1")  ,
        Friend("friend2","2") ,
        Friend("friend2","3") )

    private var dummyMessageList : List<Message>  = listOf( Message("MESSAGE 1 IT IS A VERY LONG MESSAGEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE","1", dummyFriendList[0])  ,
        Message("MESSAGE 2","2", dummyFriendList[1]) ,
        Message("MESSAGE 3","3",dummyFriendList[2]) )

    private var dummyHistList : List<History> = listOf( History("MAP","date1","RESULT") )


    override fun getAchList(size: Int, id: String): List<Achievement> {
        return dummyAchList.take(size)
    }

    override fun getHistList(size: Int, id: String): List<History> {
        return dummyHistList.take(size)
    }

    override fun getStatsList(size: Int, id: String): List<Statistic> {
        return dummyStatList.take(size)
    }

    override fun getMsgList(size: Int, id: String): List<Message> {
        return dummyMessageList.take(size)
    }

    override fun getFriendsList(size: Int, id: String): List<Friend> {
        return dummyFriendList.take(size)
    }

    override fun sendInvite(f :Friend) {
    }

    override fun sendMessage(msg : String , id : String) {
        dummyMessageList = listOf(Message(msg,"3",dummyFriendList[2]))
    }

    override fun getProfileInfo(id: String) : Friend {
        return Friend("friend0","45") //dummy Friend
    }

    override fun getActiveUser(): Friend {
        return Friend("ACTIVE","0")
    }

    override fun setDbId(id : Int) {
        this.id = id
    }

    override fun getDbId() : Int{
        return id
    }

}