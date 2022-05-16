package com.github.displace.sdp2022.profile.achievements

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R

/**
 * Adapts the recycler view to accommodate achievements
 */
class AchViewAdapter(val context: Context, private val data: List<Achievement>) :
    RecyclerView.Adapter<AchViewHolder>() {

    /**
     * Sets up the holder view with the needed values (the view itself, UI)
     *
     * @param parent : the parent of the holder
     * @param viewType : the type of the holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.ach, parent, false)
        return AchViewHolder(photoView)
    }

    /**
     * Sets up the holder view at the given position with the correct data
     *
     * @param holder : the view (one element of the list) that is part of the recycler view
     * @param position : the position of the specific holder
     */
    override fun onBindViewHolder(holder: AchViewHolder, position: Int) {
        val index = holder.adapterPosition
        "${data[index].name} : ${data[index].description}".also { holder.name.text = it }
        holder.date.text = data[index].date
    }

    /**
     * Returns the number of items in the recycler view list
     * @return : number of items in the recycler view
     */
    override fun getItemCount(): Int {
        return data.size
    }
}