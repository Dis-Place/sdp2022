package com.github.blecoeur.bootcamp.profile.achievements

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.R

public class AchViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

    val name: TextView = itemview.findViewById(R.id.statName)
    val date: TextView = itemview.findViewById(R.id.statValue)
    val icon: ImageView = itemview.findViewById(R.id.achIcon)

    lateinit var userName : String

    init{
        itemview.setOnClickListener { v ->
           Log.d("test","username is : pipo" )
         }
            //is on the view : can be used for buttons in the view

    }

}