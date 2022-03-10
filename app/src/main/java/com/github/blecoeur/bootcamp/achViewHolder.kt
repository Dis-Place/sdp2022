package com.github.blecoeur.bootcamp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

public class achViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

    val text: TextView = itemview.findViewById(R.id.examName)

}