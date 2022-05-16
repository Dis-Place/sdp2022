package com.github.displace.sdp2022.profile.messages

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.CheckConnection.checkForInternet

/**
 * The holder for the view of the message
 */
class MsgViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    /**
     * Identifiers for the content of the message, its date and the sender of the message
     * They are used in the View Adapter
     */
    val content: TextView = itemview.findViewById(R.id.msgText)
    val date: TextView = itemview.findViewById(R.id.msgDate)
    val sender: TextView = itemview.findViewById(R.id.msgSender)

    /**
     * Identifier of the reply button in the UI
     */
    val replyButton: ImageButton = itemview.findViewById(R.id.replyButton)

    /**
     * Identifier of the sender of the message
     */
    lateinit var friend: PartialUser

    init {
        /**
         * If the reply button is clicked we transition to the message sending activity
         */
        replyButton.setOnClickListener { v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply {
                putExtra("MessageReceiverID", friend.uid)
                putExtra("MessageReceiverName", friend.username)
            }
            v.context.startActivity(intent)
        }
    }

}