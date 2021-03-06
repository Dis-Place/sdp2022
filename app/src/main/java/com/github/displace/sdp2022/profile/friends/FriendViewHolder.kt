package com.github.displace.sdp2022.profile.friends

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.friendDeleter
import com.github.displace.sdp2022.profile.messageUpdater
import com.github.displace.sdp2022.profile.messages.SendMessageActivity
import com.github.displace.sdp2022.users.PartialUser


/**
 * (custom ViewHolder)
 * provides a reference and all the functionality for friends
 * Wrapper around a View, and that view is managed by RecyclerView.
 * @param itemview : the view where it will be displayed
 */
class FriendViewHolder(itemview: View, intent : Intent) : RecyclerView.ViewHolder(itemview) {

    val friendNameView: TextView = itemView.findViewById(R.id.friendName)
    val messageButton: ImageButton = itemView.findViewById(R.id.messageButton)
    val inviteButton: ImageButton = itemView.findViewById(R.id.inviteButton)
    val removeFriendButton: ImageButton = itemView.findViewById(R.id.removeFriendButton)

    lateinit var friend: PartialUser
    var tapUser : Boolean = false

    // sets up clicks on buttons listeners and their respective functionality
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
            val lobbyID = app.getLobbyID()
            val message: String = lobbyID

            val db : GoodDB = DatabaseFactory.getDB(intent)
            val activeUser = app.getActiveUser()
            var activePartialUser = PartialUser("defaultName","dummy_id")
            if(activeUser != null){
                activePartialUser = activeUser.getPartialUser()
            }
            db.runTransaction("CompleteUsers/" + friend.uid + "/MessageHistory",messageUpdater(message,activePartialUser))
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


        // remove friend button
        removeFriendButton.setOnClickListener { v ->
            Toast.makeText(removeFriendButton.context,"REMOVE FRIEND ${friend.username}", Toast.LENGTH_LONG).show()


        //    val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate(DB_URL,false) as RealTimeDatabase
            val db : GoodDB = DatabaseFactory.getDB(intent)
            val app = v.context.applicationContext as MyApplication
            val activeUser = app.getActiveUser()
            var activePartialUser = PartialUser("defaultName","dummy_id")
            if(activeUser != null){
                activePartialUser = activeUser.getPartialUser()
            }
            db.runTransaction("CompleteUsers/${activePartialUser.uid}/CompleteUser/friendsList",friendDeleter(friend))
            db.runTransaction("CompleteUsers/${friend.uid}/CompleteUser/friendsList",friendDeleter(activePartialUser))
     /*       db.getDbReference("CompleteUsers/${activePartialUser.uid}/CompleteUser/friendsList").runTransaction(
                FriendDeleter(friend)
            )
            db.getDbReference("CompleteUsers/${friend.uid}/CompleteUser/friendsList").runTransaction(
                FriendDeleter(activePartialUser)
            )*/
            friendNameView.visibility = View.GONE
            messageButton.visibility = View.GONE
            inviteButton.visibility = View.GONE
            removeFriendButton.visibility = View.GONE
        }

    }
}