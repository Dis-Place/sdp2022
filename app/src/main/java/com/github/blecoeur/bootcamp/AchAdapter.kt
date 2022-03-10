package com.github.blecoeur.bootcamp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


public class AchAdapter(context : Context, clicker : ClickListener, data : List<String> ) : RecyclerView.Adapter<achViewHolder>() {

    val list : List<String>  = data
    val context : Context = context
    var listener: ClickListener = clicker

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): achViewHolder {
      //  TODO("Not yet implemented")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.ach, parent, false)
        val viewHolder : achViewHolder  = achViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: achViewHolder, position: Int) {
    //    TODO("Not yet implemented")
        val index = holder.adapterPosition
        holder.text.setText(list[index])
        holder.itemView.setOnClickListener(View.OnClickListener { listener.click(index) })
    }

    override fun getItemCount(): Int {
     //   TODO("Not yet implemented")
        return list.size
    }
}