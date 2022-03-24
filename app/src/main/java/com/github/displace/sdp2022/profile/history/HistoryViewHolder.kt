package com.github.displace.sdp2022.profile.history

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

class HistoryViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val map: TextView = itemView.findViewById(R.id.histMap)
    val date: TextView = itemView.findViewById(R.id.histDate)
    val result: TextView = itemView.findViewById(R.id.histResult)
}