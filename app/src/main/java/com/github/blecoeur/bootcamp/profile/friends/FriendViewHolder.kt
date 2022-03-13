package com.github.blecoeur.bootcamp.profile.friends

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.ProfileDbConnection
import com.github.blecoeur.bootcamp.profile.messages.SendMessageActivity

public class FriendViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView = itemView.findViewById<TextView>(R.id.friendName)
    val messageButton = itemView.findViewById<ImageButton>(R.id.messageButton)
    val inviteButton = itemView.findViewById<ImageButton>(R.id.inviteButton)

    lateinit var dbAdapter : ProfileDbConnection;
    lateinit var friend : Friend;

    init{
        messageButton.setOnClickListener{ v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply{
                putExtra("MessageReceiverID",friend.ID)
                putExtra("MessageReceiverName",friend.name)
            }
            v.context.startActivity(intent)
        }

        inviteButton.setOnClickListener{ v -> dbAdapter.sendInvite(friend) }
    }

}