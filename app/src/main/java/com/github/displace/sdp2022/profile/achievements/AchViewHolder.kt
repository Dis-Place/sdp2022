package com.github.displace.sdp2022.profile.achievements

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

/**
 * The holder for the view of the achievement
 */
class AchViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    /**
     * Identifiers for the name (includes the description) and date of the achievement
     * They are used in the View Adapter
     */
    val name: TextView = itemview.findViewById(R.id.achName)
    val date: TextView = itemview.findViewById(R.id.achDate)


}