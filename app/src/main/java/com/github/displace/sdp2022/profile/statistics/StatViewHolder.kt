package com.github.displace.sdp2022.profile.statistics

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

/**
 * The holder for the view of a statistic
 */
class StatViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    /**
     * Identifiers for the name and value of the entry
     * They are used in the View Adapter
     */
    val name: TextView = itemview.findViewById(R.id.statName)
    val value: TextView = itemview.findViewById(R.id.statValue)
}