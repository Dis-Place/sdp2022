package com.github.blecoeur.bootcamp.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R

class NewsViewAdapter (val context : Context, val data : List<News> ) : RecyclerView.Adapter<NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.news, parent, false)
        val viewHolder : NewsViewHolder = NewsViewHolder(photoView)
        return viewHolder
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