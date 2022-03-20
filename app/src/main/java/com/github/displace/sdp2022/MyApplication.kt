package com.github.displace.sdp2022

import android.app.Application
import com.github.displace.sdp2022.news.NewsDbConnection
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend as Friend1

class MyApplication : Application() {

    //the database
    private lateinit var db: MockDB
    private lateinit var activeUser: Friend1

    fun setDb(db: MockDB) {
        this.db = db
    }

    fun getProfileDb(): ProfileDbConnection {
        return db
    }

    fun getNewsDb(): NewsDbConnection {
        return db
    }

    fun setActiveUser(user: Friend1) {
        activeUser = user
    }

    fun getActiveUser(): Friend1 {
        return activeUser
    }

}