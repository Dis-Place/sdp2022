package com.github.displace.sdp2022.profile.history

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

/**
 * The holder for the view of and game history entry
 */
class HistoryViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    /**
     * Identifiers for the game mode, date and result of the entry
     * They are used in the View Adapter
     */
    val gameMode: TextView = itemView.findViewById(R.id.histMap)
    val date: TextView = itemView.findViewById(R.id.histDate)
    val result: TextView = itemView.findViewById(R.id.histResult)
}