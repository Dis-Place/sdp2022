package com.github.displace.sdp2022.gameVersus

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.database.TransactionSpecification
import com.github.displace.sdp2022.util.DateTimeUtil
import com.github.displace.sdp2022.util.listeners.Listener

/**
 * The under-the-hood functionalities of the chat integrated into the game view
 */
class Chat(private val chatPath : String, val db : GoodDB, val view : View, val applicationContext : Context) {
    //the group of View (UI elements) that compose the chat , used to hide them as needed
    private val chatGroup : ConstraintLayout

    /**
     * A listener for the messages in the chat, will be empty if there is an error
     */
    private val chatListener = Listener<List<Map<String,Any>>?> { ls ->
        var list = listOf<Message>()
        if(ls != null){
            list = (applicationContext as MyApplication).getMessageHandler().getListOfMessages(ls)
        }
        chatUiUpdate(list)
    }

    init{
        db.addListener(chatPath,chatListener)
        chatGroup = view.findViewById(R.id.chatLayout)
    }

    /**
     * Send the message that is written in  the UI to the chat
     */
    fun addToChat(msg : Message) {

        val chatAdditionTransaction: TransactionSpecification<List<Map<String, Any>>> =
            TransactionSpecification.Builder<List<Map<String, Any>>> { ls ->
                var newLs = ls
                val msgLs = listOf(msg.toMap())
                if (newLs != null) {
                    newLs = newLs + msgLs
                    if (newLs.size >= 6) {
                        return@Builder newLs.takeLast(5)  // we only show the last 5 messages
                    }
                    return@Builder newLs
                } else {
                    return@Builder msgLs
                }
            }.onCompleteChange { committed ->
                if (committed) {
                    view.findViewById<EditText>(R.id.chatEditText).text.clear()
                }
            }.build()

        db.runTransaction(chatPath, chatAdditionTransaction)
    }
    /**
     * Update the user interface with the new messages
     * @param ls : list of messages to update
     */
    private fun chatUiUpdate(ls : List <Message>){
        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val messageAdapter = MsgViewAdapter(
            applicationContext,
            ls,
            1
        )
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    /**
     * Remove the listener for the chat.
     * Used when quitting the game or the activity is paused
     */
    fun removeListener(){
        db.removeListener(chatPath,chatListener)
    }

    /**
     * Show the chat in the game view
     */
    fun showChat(){
        chatGroup.visibility = View.VISIBLE
    }

    /**
     * Hide the chat in the game view
     */
    fun hideChat(){
        chatGroup.visibility = View.GONE

    }

}