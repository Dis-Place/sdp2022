package com.github.displace.sdp2022.profile.friends

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.FriendDeleter
import com.github.displace.sdp2022.profile.RequestAcceptor
import com.github.displace.sdp2022.profile.messageUpdater
import com.github.displace.sdp2022.profile.messages.SendMessageActivity
import com.github.displace.sdp2022.users.PartialUser

class FriendViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val friendNameView: TextView = itemView.findViewById(R.id.friendName)
    val messageButton: ImageButton = itemView.findViewById(R.id.messageButton)
    val inviteButton: ImageButton = itemView.findViewById(R.id.inviteButton)
    val removeFriendButton: ImageButton = itemView.findViewById(R.id.removeFriendButton)

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
            val lobbyID = app.getLobbyID()
            val message: String = lobbyID

          //  val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
            val intent : Intent = Intent()
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

        removeFriendButton.setOnClickListener { v ->
            Toast.makeText(removeFriendButton.context,"REMOVE FRIEND ${friend.username}", Toast.LENGTH_LONG).show()


            val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
            val app = v.context.applicationContext as MyApplication
            val activeUser = app.getActiveUser()
            var activePartialUser = PartialUser("defaultName","dummy_id")
            if(activeUser != null){
                activePartialUser = activeUser.getPartialUser()
            }
//            db.getDbReference("CompleteUsers/${activePartialUser.uid}/CompleteUser/friendsList").runTransaction(
//                FriendDeleter(friend)
//            )
            db.getDbReference("CompleteUsers/${friend.uid}/CompleteUser/friendsList").runTransaction(
                FriendDeleter(activePartialUser)
            )
            activeUser?.removeFriend(friend)
            friendNameView.visibility = View.GONE
            messageButton.visibility = View.GONE
            inviteButton.visibility = View.GONE
            removeFriendButton.visibility = View.GONE
        }

    }
}