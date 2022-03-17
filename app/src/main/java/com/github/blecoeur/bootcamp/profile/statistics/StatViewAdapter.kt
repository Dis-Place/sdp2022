package com.github.blecoeur.bootcamp.profile.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R

class StatViewAdapter(val context : Context, private val data : List<Statistic> ) : RecyclerView.Adapter<StatViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.stat, parent, false)
        return StatViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        //    TODO("Not yet implemented")
        val index = holder.adapterPosition
        holder.name.text = data[index].name
        holder.value.text = data[index].value.toString()
    }

    override fun getItemCount(): Int {
        //   TODO("Not yet implemented")
        return data.size
    }
}