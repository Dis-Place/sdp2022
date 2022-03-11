package com.github.blecoeur.bootcamp.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.achievements.AchViewHolder
import com.github.blecoeur.bootcamp.profile.statistics.StatViewHolder

class FriendViewAdapter(val context : Context, val data : List<Friend>, val dbAdapter : DBFriendAdapter): RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        val viewHolder : FriendViewHolder = FriendViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        //    TODO("Not yet implemented")
        val index = holder.adapterPosition
        holder.friendNameView.text = data[index].name
        holder.friend = data[index]
        holder.dbAdapter = dbAdapter
    }

    override fun getItemCount(): Int {
        //   TODO("Not yet implemented")
        return data.size
    }

}