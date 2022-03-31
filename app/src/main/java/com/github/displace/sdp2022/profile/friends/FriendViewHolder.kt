package com.github.displace.sdp2022.profile.friends

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.profile.messages.SendMessageActivity
import com.github.displace.sdp2022.users.PartialUser

class FriendViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView: TextView = itemView.findViewById(R.id.friendName)
    val messageButton: ImageButton = itemView.findViewById(R.id.messageButton)
    val inviteButton: ImageButton = itemView.findViewById(R.id.inviteButton)

    lateinit var dbAdapter: ProfileDbConnection
    lateinit var friend: PartialUser

    init {
        messageButton.setOnClickListener { v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply {
                putExtra("MessageReceiverID", friend.uid)
                putExtra("MessageReceiverName", friend.username)
            }
            v.context.startActivity(intent)
        }

        inviteButton.setOnClickListener { dbAdapter.sendInvite(friend) }

        itemview.setOnClickListener { v ->
            val intent = Intent(v.context, FriendProfile::class.java).apply {
                putExtra("FriendId", friend.uid)
                putExtra("FriendUsername", friend.username)
            }
            v.context.startActivity(intent)
        }

    }
}