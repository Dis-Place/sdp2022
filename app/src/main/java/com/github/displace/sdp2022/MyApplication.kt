package com.github.displace.sdp2022

import android.app.Application
import com.github.displace.sdp2022.profile.messages.MessageHandler
import com.github.displace.sdp2022.users.CompleteUser
import java.text.SimpleDateFormat
import java.util.*

/**
 * To be used as the application context during the execution. Allows to communicate between activities seamlessly.
 */
class MyApplication : Application() {

    //the user that is currently active in the application
    private var completeUser: CompleteUser? = null

    //the private lobby that is currently active (if there is one) - used to communicate between MatchMaking and FriendViewHolder
    private var lobbyID : String = ""

    //the message handler that is used currently active : used to dispatch notifications
    private lateinit var msgHandler : MessageHandler

    /**
     * @param ID : the new lobby ID in which the user is
     */
    fun setLobbyID(ID : String){
        lobbyID = ID
    }

    /**
     * @return the lobbyID in which the user currently is
     */
    fun getLobbyID(): String {
        return lobbyID
    }

    /**
     * @param handler : the new message handler of the execution
     */
    fun setMessageHandler(handler : MessageHandler){
        msgHandler = handler
    }

    /**
     * @return the current message handler in the execution
     */
    fun getMessageHandler(): MessageHandler {
        return msgHandler
    }

    /**
     * @param user : the new active user of the execution
     */
    fun setActiveUser(user: CompleteUser?) {
        completeUser = user
    }

    /**
     * @return the current active user in the execution
     */
    fun getActiveUser(): CompleteUser? {
        return completeUser
    }

}