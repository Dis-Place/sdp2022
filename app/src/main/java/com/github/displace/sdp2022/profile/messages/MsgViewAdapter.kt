package com.github.displace.sdp2022.profile.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

class MsgViewAdapter(
    val context: Context,
    private val data: List<Message>,
    private val type : Int
) : RecyclerView.Adapter<MsgViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.msg, parent, false)
        return MsgViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val index = holder.adapterPosition
        holder.content.text = data[index].message
        holder.sender.text = data[index].sender.username
        holder.date.text = data[index].date
        holder.friend = data[index].sender

        when(type){
            1 -> {
                holder.replyButton.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}