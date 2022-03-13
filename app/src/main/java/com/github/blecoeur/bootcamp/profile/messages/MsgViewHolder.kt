package com.github.blecoeur.bootcamp.profile.messages

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.NewsActivity
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
        replyButton.setOnClickListener{ v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply{
                putExtra("MessageReceiverID",friend.ID)
                putExtra("MessageReceiverName",friend.name)
            }
            v.context.startActivity(intent)
        }
    }

}