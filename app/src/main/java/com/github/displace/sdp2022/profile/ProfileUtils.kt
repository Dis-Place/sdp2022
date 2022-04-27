package com.github.displace.sdp2022.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*

class MessageUpdater(val custom : Boolean,val applicationContext : Context, val message : String, val activePartialUser : PartialUser ) : Transaction.Handler {
    val app = applicationContext as MyApplication
    override fun doTransaction(currentData: MutableData): Transaction.Result {
        val ls = currentData.value as ArrayList<MutableMap<String,Any>>?
        val msg = Message(message,app.getCurrentDate(), activePartialUser)
        if(ls == null){
            return Transaction.success(currentData)
        }else{
            val msgMap = HashMap<String,Any>()
            msgMap["message"] = msg.message
            msgMap["date"] = app.getCurrentDate()
            msgMap["sender"] = msg.sender
            ls.add(0,msgMap)
        }
        currentData.value = ls
        return Transaction.success(currentData)
    }

    override fun onComplete(
        error: DatabaseError?,
        committed: Boolean,
        currentData: DataSnapshot?
    ) {
    }

}


class FriendRequest {
    companion object {

        private const val TAG = "FriendRequest"
        private lateinit var rootRef : DatabaseReference
        // target is the user name
        fun sendFriendRequest(target : String, rootRef: DatabaseReference, currentUser : PartialUser) {

            this.rootRef = rootRef

            Log.d(TAG,"CHECK IF USER $target EXISTS")
            val usersRef: DatabaseReference = rootRef.child("CompleteUsers")

            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    var partialUsers = getPartialUsers(dataSnapshot)
                    if( checkUserExists(partialUsers, target) ){
                        val source = currentUser
                        val target = getTargetUser(dataSnapshot, partialUsers, target)
                        sendInvite(source, target)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            usersRef.addListenerForSingleValueEvent(eventListener)


        }

        fun sendInvite(source : PartialUser, target : PartialUser){
            val inviteDbRef = rootRef.child("Invites")
            val invite = Invite(source, target)
            inviteDbRef.push().setValue(invite)
        }

        fun checkUserExists(users : List<PartialUser>, target : String) : Boolean {
            Log.d(TAG, "Searching if $target")
            for (user in users) {
                if( user.username == target ){
                    return true
                }
            }
            return false
        }

        fun getPartialUsers(dataSnapshot: DataSnapshot) : MutableList<PartialUser>{
            var partialUsers = mutableListOf<PartialUser>()
            for (ds in dataSnapshot.children) {
                val uid = ds.child("CompleteUser").child("partialUser").child("uid").value.toString()
                val username = ds.child("CompleteUser").child("partialUser").child("username").value.toString()

                val partialUser = PartialUser(username,uid)
//                    Log.d(TAG, partialUser.toString())
                partialUsers.add(partialUser)
            }
            return partialUsers
        }

        fun getTargetUser(dataSnapshot: DataSnapshot, users : List<PartialUser>, target : String) : PartialUser {
            for (user in users) {
                if( user.username == target ){
                    return user
                }
            }
            return PartialUser("empty", "empty") // never gets executed as we check that the user exists before
        }
    }
}