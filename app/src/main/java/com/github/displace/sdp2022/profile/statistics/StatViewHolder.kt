package com.github.displace.sdp2022.profile.statistics

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

class StatViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val name: TextView = itemview.findViewById(R.id.statName)
    val value: TextView = itemview.findViewById(R.id.statValue)
}