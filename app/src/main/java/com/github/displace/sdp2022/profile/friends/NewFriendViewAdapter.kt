package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser

/**
 * Adapter Class that associates new friend
 * @param context : context where the view holder will be displayed
 * @param data : list of new friends
 * @param MM : int indicator for what buttons of friend to show
 */
class NewFriendViewAdapter(
    val context: Context,
    private val data: List<PartialUser>,
    private val MM : Int
) : RecyclerView.Adapter<NewFriendViewHolder>() {


    /**
     *  Create new views (invoked by the layout manager)
     *  @param parent : parent of the created views
     *  @param viewType : type of the view
     *  @return ViewHolder : holds the views
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendViewHolder {
        print("data : $data")
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        return NewFriendViewHolder(photoView, context)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder : provides functionality to the view
     * @param position : position of the element in the list
     */
    override fun onBindViewHolder(holder: NewFriendViewHolder, position: Int) {
        print(data)
        val index = holder.adapterPosition
        holder.friendNameView.text = data[index].username
        holder.friend = data[index]
        when (MM) {
            0 -> {
                holder.inviteButton.visibility = View.INVISIBLE
                holder.tapUser = true
            }
            1 -> {
                holder.messageButton.visibility = View.INVISIBLE
                holder.tapUser = false
            }
            2 -> {
                holder.messageButton.visibility = View.INVISIBLE
                holder.inviteButton.visibility = View.INVISIBLE
                holder.tapUser = false
            }
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return number of items in the dataset
     */
    override fun getItemCount(): Int {
        return data.size
    }

}