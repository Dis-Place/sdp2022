package com.github.displace.sdp2022.profile.messages

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser

class MsgViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val content: TextView = itemview.findViewById(R.id.msgText)
    val date: TextView = itemview.findViewById(R.id.msgDate)
    val sender: TextView = itemview.findViewById(R.id.msgSender)

    private val replyButton: ImageButton = itemview.findViewById(R.id.replyButton)


    lateinit var dbAdapter: ProfileDbConnection
    lateinit var friend: PartialUser

    init {
        replyButton.setOnClickListener { v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply {
                putExtra("MessageReceiverID", friend.uid)
                putExtra("MessageReceiverName", friend.username)
            }
            v.context.startActivity(intent)
        }
    }

}