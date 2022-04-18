package com.github.displace.sdp2022

import android.app.Application
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import java.text.SimpleDateFormat
import java.util.*

class MyApplication : Application() {


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


    fun setActiveUser(user: CompleteUser) {
        completeUser = user
    }

    fun getActiveUser(): CompleteUser? {
        return completeUser
    }

}