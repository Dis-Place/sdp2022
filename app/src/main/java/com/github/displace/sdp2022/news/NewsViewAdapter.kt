package com.github.displace.sdp2022.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R

class NewsViewAdapter (val context : Context, private val data : List<News> ) : RecyclerView.Adapter<NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.news, parent, false)
        return NewsViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        //    TODO("Not yet implemented")
        val index = holder.adapterPosition
        holder.title.text = data[index].title
        holder.description.text = data[index].description
        holder.date.text = data[index].date
    }

    override fun getItemCount(): Int {
        //   TODO("Not yet implemented")
        return data.size
    }

}