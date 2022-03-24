package com.github.displace.sdp2022.profile.achievements

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R


class AchViewAdapter(val context: Context, private val data: List<Achievement>) :
    RecyclerView.Adapter<AchViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchViewHolder {
        //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.ach, parent, false)
        return AchViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: AchViewHolder, position: Int) {
        //    TODO("Not yet implemented")
        val index = holder.adapterPosition
        holder.name.text = data[index].name
        holder.date.text = data[index].date
    }

    override fun getItemCount(): Int {
        //   TODO("Not yet implemented")
        return data.size
    }
}