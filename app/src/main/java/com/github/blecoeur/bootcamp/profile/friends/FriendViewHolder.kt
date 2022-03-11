package com.github.blecoeur.bootcamp.profile.friends

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R

public class FriendViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView = itemView.findViewById<TextView>(R.id.friendName)
    val messageButton = itemView.findViewById<ImageButton>(R.id.inviteButton)
    val inviteButton = itemView.findViewById<ImageButton>(R.id.messageButton)

    lateinit var dbAdapter : DBFriendAdapter;
    lateinit var friend : Friend;

    init{
        messageButton.setOnClickListener{ v -> dbAdapter.sendFriendMessage(friend) }
        inviteButton.setOnClickListener{ v -> dbAdapter.sendFriendInvite(friend) }
    }

}