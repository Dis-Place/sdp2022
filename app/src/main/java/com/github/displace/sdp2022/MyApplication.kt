package com.github.displace.sdp2022

import android.app.Application
import android.content.SharedPreferences
import com.github.displace.sdp2022.news.NewsDbConnection
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.users.CompleteUser

class MyApplication : Application() {

    //the database
    private lateinit var db: MockDB

    private var completeUser: CompleteUser? = null

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