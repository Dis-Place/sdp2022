package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.R

class FriendViewAdapter(
    val context: Context,
    private val data: List<Friend>,
    private val dbAdapter: ProfileDbConnection
) : RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        return FriendViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val index = holder.adapterPosition
        holder.friendNameView.text = data[index].name
        holder.friend = data[index]
        holder.dbAdapter = dbAdapter
    }

    override fun getItemCount(): Int {
        return data.size
    }

}