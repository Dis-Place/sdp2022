package com.github.displace.sdp2022.news

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

class NewsViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val title: TextView = itemview.findViewById(R.id.newsTitle)
    val description: TextView = itemview.findViewById(R.id.newsDescription)
    val date: TextView = itemview.findViewById(R.id.newsDate)
    val image: ImageView = itemview.findViewById(R.id.newsImage)

}