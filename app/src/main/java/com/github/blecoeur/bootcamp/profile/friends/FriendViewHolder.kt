package com.github.blecoeur.bootcamp.profile.friends

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.ProfileDbConnection

public class FriendViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView = itemView.findViewById<TextView>(R.id.friendName)
    val messageButton = itemView.findViewById<ImageButton>(R.id.inviteButton)
    val inviteButton = itemView.findViewById<ImageButton>(R.id.messageButton)

    lateinit var dbAdapter : ProfileDbConnection;
    lateinit var friend : Friend;

    init{
        messageButton.setOnClickListener{ v -> dbAdapter.sendMessage("use the view in parameer to get it",friend) }
        inviteButton.setOnClickListener{ v -> dbAdapter.sendInvite(friend) }
    }

}