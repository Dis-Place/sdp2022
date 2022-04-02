package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.profile.ProfileDbConnection
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser

class FriendViewAdapter(
    val context: Context,
    private val data: List<PartialUser>,
    private val MM : Int
) : RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        return FriendViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val index = holder.adapterPosition
        holder.friendNameView.text = data[index].username
        holder.friend = data[index]
        when (MM) {
            0 -> {
                holder.inviteButton.visibility = View.INVISIBLE
                holder.tapUser = true
            }
            1 -> {
                holder.messageButton.visibility = View.INVISIBLE
                holder.tapUser = false
            }
            2 -> {
                holder.messageButton.visibility = View.INVISIBLE
                holder.inviteButton.visibility = View.INVISIBLE
                holder.tapUser = false
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}