package com.github.blecoeur.bootcamp.profile.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.friends.FriendViewHolder

class HistoryViewAdapter(val context : Context, val data : List<History>): RecyclerView.Adapter<HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.hist, parent, false)
        val viewHolder : HistoryViewHolder = HistoryViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val index = holder.adapterPosition
        holder.map.text = data[index].map
        holder.date.text = data[index].date
        holder.result.text = data[index].result
    }

    override fun getItemCount(): Int {
        return data.size
    }

}