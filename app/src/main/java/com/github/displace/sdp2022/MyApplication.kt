package com.github.displace.sdp2022

import android.app.Application
import android.content.SharedPreferences
import com.github.displace.sdp2022.news.NewsDbConnection
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend

class MyApplication : Application() {

    //the database
    private lateinit var db: MockDB
    private lateinit var activeUser: Friend

    fun setDb(db: MockDB) {
        this.db = db
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