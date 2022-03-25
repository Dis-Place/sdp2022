package com.github.displace.sdp2022

import android.app.Application
import com.github.displace.sdp2022.news.NewsDbConnection
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend

class MyApplication : Application() {

    //the database
    private lateinit var db: MockDB
    private lateinit var activeUser: Friend

    private lateinit var rTdb : RealTimeDatabase
    private lateinit var rTdbNoCache : RealTimeDatabase

    fun setDbRt(db: RealTimeDatabase) {
        this.rTdb = db
    }

    fun setDb(db: MockDB) {
        this.db= db
    }

    fun setDbNonCache(db: RealTimeDatabase) {
        this.rTdbNoCache = db
    }

    fun getDb() : RealTimeDatabase{
        return rTdb
    }

    fun getDbNonCache() : RealTimeDatabase{
        return rTdbNoCache
    }


    fun getProfileDb(): ProfileDbConnection {
        return db
    }

    fun getNewsDb(): NewsDbConnection {
        return db
    }

    fun setActiveUser(user: Friend) {
        activeUser = user
    }

    fun getActiveUser(): Friend {
        return activeUser
    }

}