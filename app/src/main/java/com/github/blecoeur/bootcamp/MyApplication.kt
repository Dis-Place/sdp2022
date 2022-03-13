package com.github.blecoeur.bootcamp

import android.app.Application
import com.github.blecoeur.bootcamp.profile.ProfileDbConnection

class MyApplication : Application() {

    private lateinit var profileDB : ProfileDbConnection;

    fun setProfileDb( db : ProfileDbConnection) {
        profileDB = db
    }

    fun getProfileDb() : ProfileDbConnection {
        return profileDB
    }

}