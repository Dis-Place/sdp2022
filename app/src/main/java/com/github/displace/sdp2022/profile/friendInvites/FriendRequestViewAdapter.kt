package com.github.displace.sdp2022.profile.friendInvites

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.*
import com.github.displace.sdp2022.util.CheckConnectionUtil.checkForInternet

/**
 * Adapter Class that associates the friend requests with the ViewHolder views.
 * @param dataSet : list of friend requests
 * @param context : context where the view holder will be displayed
 */
class FriendRequestViewAdapter(private var dataSet: MutableList<InviteWithId>, private val context: ProfileActivity?) :
    RecyclerView.Adapter<FriendRequestViewAdapter.ViewHolder>() {

    val TAG : String = "FriendRequestViewAdapter" // tag for debugging

    /**
     * (custom ViewHolder)
     * provides a reference and all the functionality for friend requests.
     * Wrapper around a View, and that view is managed by RecyclerView.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rejectButton : Button = itemView.findViewById<Button>(R.id.rejectRequestButton)
        val acceptButton : Button = itemView.findViewById<Button>(R.id.acceptRequestButton)
        val textView: TextView = view.findViewById(R.id.requestSourceText)


        // set on on click listeners for the different buttons
        init{
            // button to reject a friend request, will delete the invite from the database as well
            rejectButton.setOnClickListener{
                if(context != null && !checkForInternet(context)) {
                    context.setStatus(false)
                    Toast.makeText(context, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
                } else {
                    Log.d(TAG, " REJECTING FRIEND OFFER")
                    val inviteId = deleteRequest(adapterPosition)
                    DeleteInvite.deleteInvite(inviteId.id)
                }
            }

            // button to accept the friend request
            acceptButton.setOnClickListener{
                if(!checkForInternet(context as Context)) {
                    Toast.makeText(context, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
                } else {
                    Log.d(TAG, " ACCEPTING FRIEND OFFER")
                    val invite = deleteRequest(adapterPosition)


                    val db: RealTimeDatabase = RealTimeDatabase().noCacheInstantiate(
                        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
                        false
                    ) as RealTimeDatabase
                    db.getDbReference("CompleteUsers/${invite.invite.source.uid}/CompleteUser/friendsList")
                        .runTransaction(
                            RequestAcceptor(invite.invite.target)
                        )
//                    db.getDbReference("CompleteUsers/${invite.invite.target.uid}/CompleteUser/friendsList")
//                        .runTransaction(
//                            RequestAcceptor(invite.invite.source)
//                        )
                    val app = acceptButton.context.applicationContext as MyApplication
                    val user = app.getActiveUser()!!
                    Log.d("Friend", "add friend : ${invite.invite.source} ")
                    user.addFriend(invite.invite.source, true)

                    DeleteInvite.deleteInvite(invite.id)
                }
            }
        }
    }

    /**
     *  Create new views (invoked by the layout manager)
     *  @param viewGroup : parent of the created views
     *  @param viewType : type of the view
     *  @return ViewHolder : holds the views
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item : friend_request
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_request, viewGroup, false)

        return ViewHolder(view)
    }


    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param viewHolder : provides functionality to the view
     * @param position : position of the element in the list
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].invite.source.username

    }


    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return number of items in the dataset
     */
    override fun getItemCount() = dataSet.size

    /**
     * deletes a request from the the adapter and returns the deleted inviteWithId
     * @param index : index of the element to remove
     * @return the invite we just deleted from the adapter
     */
    fun deleteRequest(index: Int) : InviteWithId {
        val inviteWithIdToDelete = dataSet[index]
        dataSet.removeAt(index)
        notifyDataSetChanged()
        return inviteWithIdToDelete
    }
}
