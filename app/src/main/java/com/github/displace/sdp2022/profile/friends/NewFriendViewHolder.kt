package com.github.displace.sdp2022.profile.friends

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.profile.messages.SendMessageActivity
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.FirebaseDatabase


/**
 * (custom ViewHolder)
 * provides a reference and all the functionality for new friends
 * Wrapper around a View, and that view is managed by RecyclerView.
 * @param itemview : the view where it will be displayed
 * @param context : context to display the items in
 */
class NewFriendViewHolder(itemview: View, context: Context) : RecyclerView.ViewHolder(itemview) {

    val friendNameView: TextView = itemView.findViewById(R.id.friendName)
    val messageButton: ImageButton = itemView.findViewById(R.id.messageButton)
    val inviteButton: ImageButton = itemView.findViewById(R.id.inviteButton)

    lateinit var friend: PartialUser
    var tapUser : Boolean = false

    init {
        messageButton.setOnClickListener { v ->
            val intent = Intent(v.context, SendMessageActivity::class.java).apply {
                putExtra("MessageReceiverID", friend.uid)
                putExtra("MessageReceiverName", friend.username)
            }
            v.context.startActivity(intent)
        }

        inviteButton.setOnClickListener { v ->
            val app = v.context.applicationContext as MyApplication

            val activeUser = app.getActiveUser()
            var activePartialUser = PartialUser("defaultName","dummy_id")
            if(activeUser != null){
                activePartialUser = activeUser.getPartialUser()
            }

            FriendRequest.sendFriendRequest(context, friend.username, FirebaseDatabase.getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/").reference,
                activePartialUser
            )
        }

        itemview.setOnClickListener { v ->
            if(!tapUser){
                return@setOnClickListener
            }
            val intent = Intent(v.context, FriendProfile::class.java).apply {
                putExtra("FriendId", friend.uid)
                putExtra("FriendUsername", friend.username)
            }
            v.context.startActivity(intent)
        }

    }
}