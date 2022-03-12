package com.github.blecoeur.bootcamp.profile.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.ProfileDbConnection
import com.github.blecoeur.bootcamp.profile.friends.FriendViewHolder

class MsgViewAdapter( val context : Context, val data : List<Message>, val dbAdapter : ProfileDbConnection) : RecyclerView.Adapter<MsgViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.msg, parent, false)
        val viewHolder : MsgViewHolder = MsgViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val index = holder.adapterPosition
        holder.content.text = data[index].message
        holder.sender.text = data[index].sender.name
        holder.date.text = data[index].date
        holder.dbAdapter = dbAdapter
        holder.friend = data[index].sender

    }

    override fun getItemCount(): Int {
        return data.size
    }
}