package com.github.displace.sdp2022.profile.friends

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.messages.SendMessageActivity

class FriendViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView: TextView = itemView.findViewById(R.id.friendName)
    private val messageButton: ImageButton = itemView.findViewById(R.id.messageButton)
    private val inviteButton: ImageButton = itemView.findViewById(R.id.inviteButton)

    lateinit var dbAdapter: ProfileDbConnection
    lateinit var friend: Friend

    init {
        messageButton.setOnClickListener { v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply {
                putExtra("MessageReceiverID", friend.ID)
                putExtra("MessageReceiverName", friend.name)
            }
            v.context.startActivity(intent)
        }

        inviteButton.setOnClickListener { dbAdapter.sendInvite(friend) }

        itemview.setOnClickListener { v ->
            val intent = Intent(v.context, FriendProfile::class.java).apply {
                putExtra("FriendId", friend.ID)
                putExtra("FriendUsername", friend.name)
            }
            v.context.startActivity(intent)
        }

    }
}