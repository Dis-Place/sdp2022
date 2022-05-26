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


class FriendRequestViewAdapter(private var dataSet: MutableList<InviteWithId>, private val context: ProfileActivity?) :
    RecyclerView.Adapter<FriendRequestViewAdapter.ViewHolder>() {

    val TAG : String = "FriendRequestViewAdapter"

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rejectButton : Button = itemView.findViewById<Button>(R.id.rejectRequestButton)
        val acceptButton : Button = itemView.findViewById<Button>(R.id.acceptRequestButton)
        val textView: TextView = view.findViewById(R.id.requestSourceText)

        init{
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
            acceptButton.setOnClickListener{
                if(!checkForInternet(context as Context)) {
                    Toast.makeText(context, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
                } else {
                    Log.d(TAG, " ACCEPTING FRIEND OFFER")
                    val invite = deleteRequest(adapterPosition)


//                val app = acceptButton.context.applicationContext as MyApplication
//                val user = app.getActiveUser()!!
//                user.addFriend(invite.invite.source)

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
                    user.addFriend(invite.invite.source)

                    DeleteInvite.deleteInvite(invite.id)
                }
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_request, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].invite.source.username.toString()

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun deleteRequest(index: Int) : InviteWithId {
        val inviteWithIdToDelete = dataSet[index]
        dataSet.removeAt(index)
        notifyDataSetChanged()
        return inviteWithIdToDelete
    }

}
