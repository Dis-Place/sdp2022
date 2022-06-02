package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser


/**
 * Adapter Class that associates users friends list with ViewHolder views.
 * @param context : context where the view holder will be displayed
 * @param data : list of friends
 * @param MM : indicator int for what buttons of a friend get displayed
 */
class FriendViewAdapter(
    val context: Context,
    private val data: List<PartialUser>,
    private val MM : Int
) : RecyclerView.Adapter<FriendViewHolder>() {
    private lateinit var curHolder : FriendViewHolder


    /**
     *  Create new views (invoked by the layout manager)
     *  @param parent : parent of the created views
     *  @param viewType : type of the view
     *  @return ViewHolder : holds the views
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val parentContext = parent.context
        val inflater = LayoutInflater.from(parentContext)

        val photoView: View = inflater.inflate(R.layout.friend, parent, false)
        return FriendViewHolder(photoView)
    }


    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder : provides functionality to the view
     * @param position : position of the element in the list
     */
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        curHolder = holder
        val index = holder.adapterPosition
        holder.friendNameView.text = data[index].username
        holder.friend = data[index]
        when (MM) {
            0 -> {  // in profile
                holder.inviteButton.visibility = View.INVISIBLE
                holder.removeFriendButton.visibility = View.VISIBLE
                holder.tapUser = true
            }
            1 -> {  // in lobby as friend
                holder.messageButton.visibility = View.INVISIBLE
                holder.removeFriendButton.visibility = View.INVISIBLE
                holder.tapUser = false
            }
            2 -> {  // In lobby as active user
                holder.messageButton.visibility = View.INVISIBLE
                holder.inviteButton.visibility = View.INVISIBLE
                holder.removeFriendButton.visibility = View.INVISIBLE
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