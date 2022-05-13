package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser

class NewFriendViewAdapter(
    val context: Context,
    private val data: List<PartialUser>,
    private val MM : Int
) : RecyclerView.Adapter<NewFriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendViewHolder {
        print("data : $data")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        return NewFriendViewHolder(photoView, context)
    }

    override fun onBindViewHolder(holder: NewFriendViewHolder, position: Int) {
        print(data)
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