package com.github.blecoeur.bootcamp.profile.messages

import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.ProfileDbConnection
import com.github.blecoeur.bootcamp.profile.friends.Friend

class MsgViewHolder (itemview : View) : RecyclerView.ViewHolder(itemview) {

    val content : TextView = itemview.findViewById(R.id.msgText)
    val date : TextView = itemview.findViewById(R.id.msgDate)
    val sender : TextView = itemview.findViewById(R.id.msgSender)

    val replyButton : ImageButton = itemview.findViewById(R.id.replyButton)

    lateinit var dbAdapter : ProfileDbConnection;
    lateinit var friend : Friend;

    init{
        replyButton.setOnClickListener{ v -> dbAdapter.sendMessage("use the view in parameter to get it",friend) }
    }

}