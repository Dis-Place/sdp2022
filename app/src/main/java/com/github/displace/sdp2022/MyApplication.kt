package com.github.displace.sdp2022

import android.app.Application
import android.content.SharedPreferences
import com.github.displace.sdp2022.news.NewsDbConnection
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import java.text.SimpleDateFormat
import java.util.*

class MyApplication : Application() {

    //the database
    private lateinit var db: MockDB

    private var completeUser: CompleteUser? = null

    private var lobbyID : String = ""

    private lateinit var msgHandler : MessageHandler

    fun setLobbyID(ID : String){
        lobbyID = ID
    }

    fun getLobbyID(): String {
        return lobbyID
    }

    fun setMessageHandler(handler : MessageHandler){
        msgHandler = handler
    }

    fun getMessageHandler(): MessageHandler {
        return msgHandler
    }

    fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }

    fun getCurrentTime(): String {
        val simpleTime = SimpleDateFormat("HH:mm")
        return simpleTime.format(Date())
    }

  //  private lateinit var rTdb : RealTimeDatabase
   // private lateinit var rTdbNoCache : RealTimeDatabase

  /*  fun setDbRt(db: RealTimeDatabase) {
        this.rTdb = db
    }*/

    fun setDb(db: MockDB) {
        this.db= db
    }
/*
    fun setDbNonCache(db: RealTimeDatabase) {
        this.rTdbNoCache = db
    }*/

  /*  fun getDb() : RealTimeDatabase{
        return rTdb
    }
*/
  /*  fun getDbNonCache() : RealTimeDatabase{
        return rTdbNoCache
    }
*/

    fun getProfileDb(): ProfileDbConnection {
        return db
    }

    fun getNewsDb(): NewsDbConnection {
        return db
    }

    fun setActiveUser(user: CompleteUser) {
        completeUser = user
    }

    fun getActiveUser(): CompleteUser? {
        return completeUser
    }

}