package com.github.displace.sdp2022.profile.achievements

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

class AchViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val name: TextView = itemview.findViewById(R.id.achName)
    val date: TextView = itemview.findViewById(R.id.achDate)

    init {
        itemview.setOnClickListener { v ->
            Log.d("test", "username is : pipo")
        }
        //is on the view : can be used for buttons in the view

    }

}