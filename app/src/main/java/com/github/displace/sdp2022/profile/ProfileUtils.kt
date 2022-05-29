package com.github.displace.sdp2022.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import android.widget.Toast

import com.github.displace.sdp2022.database.TransactionSpecification
import com.github.displace.sdp2022.profile.friendInvites.Invite
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.DateTimeUtil
import com.google.firebase.database.*

/**
 * A class that represent the transaction done while sending a message
 * It is used in the SendMessageActivity and to create invitations for private lobbies in MatchMaking
 *
 * @param message : the message content
 * @param activePartialUser : the current user, which is the sender of the message
 */
fun messageUpdater(message : String, activePartialUser : PartialUser) : TransactionSpecification<ArrayList<MutableMap<String,Any>>> =
    TransactionSpecification.Builder<ArrayList<MutableMap<String,Any>>> { ls ->

        val msg = Message(message, DateTimeUtil.currentDate(), activePartialUser)
        if(ls != null){
            val msgMap = HashMap<String,Any>()
            msgMap["message"] = msg.message
            msgMap["date"] = DateTimeUtil.currentDate()
            msgMap["sender"] = msg.sender
            ls.add(0,msgMap)
        }
        return@Builder ls

    }.build()

class FriendRequest {
    companion object {

        private const val TAG = "FriendRequest"
        private var invitesLiveData = MutableLiveData<MutableList<InviteWithId>>()
        private lateinit var rootRef : DatabaseReference

        // target is the user name
        fun sendFriendRequest(
            context : Context,
            target: String,
            rootRef: DatabaseReference,
            currentUser: PartialUser
        ) {

            this.rootRef = rootRef

            Log.d(TAG, "CHECK if $target exists")
            val usersRef: DatabaseReference = rootRef.child("CompleteUsers")
            val InvitesRef : DatabaseReference = rootRef.child("Invites")

            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var partialUsers = getPartialUsers(dataSnapshot)
                    if (checkUserExists(partialUsers, target)) {
                        val source = currentUser
                        val target = getTargetUser( partialUsers, target)
                        if( alreadyInvited(FriendRequest.invitesLiveData, source, target)){
                            Toast.makeText(context,"Already invited ${target.username} ", Toast.LENGTH_LONG).show()
                        }
                        else{
                            sendInvite(source, target)
                            Toast.makeText(context,"Invite to ${target.username} sent", Toast.LENGTH_LONG).show()

                            }
                        }
                    else{
                        Log.d(TAG, "$target DOES NOTEXISTS")
                        Toast.makeText(context,"User $target does not exist", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            }

            val eventInviteListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    var invites = ReceiveFriendRequests.getInvites(dataSnapshot)

                    invitesLiveData.value = invites
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            InvitesRef.addListenerForSingleValueEvent(eventInviteListener)



            usersRef.addListenerForSingleValueEvent(eventListener)


        }

        fun sendInvite(source : PartialUser, target : PartialUser){
            val inviteDbRef = rootRef.child("Invites")
            val invite = Invite(source, target)
            inviteDbRef.push().setValue(invite)
        }

        fun checkUserExists(users: List<PartialUser>, target: String): Boolean {
            Log.d(TAG, "Searching if $target")
            for (user in users) {
                if (user.username == target) {
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

        fun getTargetUser( users : List<PartialUser>, target : String) : PartialUser {
            for (user in users) {
                if( user.username == target ){
                    return user
                }
            }
            return PartialUser("empty", "empty") // never gets executed as we check that the user exists before
        }

        fun alreadyInvited(invitesWithId : MutableLiveData<MutableList<InviteWithId>>, source: PartialUser, target: PartialUser ) : Boolean {
            val inviteDirection1 = Invite(source, target)
            val inviteDirection2 = Invite(target, source)
            for( inviteWithId in invitesWithId.value!!){
                if(inviteWithId.invite == inviteDirection1 ||  inviteWithId.invite == inviteDirection2){
                    return true
                }
            }
            return false
        }


    }


}




class ReceiveFriendRequests {
    companion object {

        private var invitesLiveData = MutableLiveData<MutableList<InviteWithId>>()
        private const val TAG = "ReceiveFriendRequests"
        private lateinit var rootRef : DatabaseReference
        // target is the user name

        fun receiveRequests(rootRef: DatabaseReference, currentUser : PartialUser) : MutableLiveData<MutableList<InviteWithId>> {
            var currentUserInvites = mutableListOf<InviteWithId>()
            this.rootRef = rootRef

            Log.d(TAG,"CHECK IF USER ${currentUser.username.toString()} has friend requests")
            val inviteRef: DatabaseReference = rootRef.child("Invites")

            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    var invites = getInvites(dataSnapshot)
                    currentUserInvites = getUserInvites(invites, currentUser.uid)
                    invitesLiveData.value = currentUserInvites
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            inviteRef.addListenerForSingleValueEvent(eventListener)
            return invitesLiveData

        }

        fun getUserInvites(invites : MutableList<InviteWithId>, userUid : String) : MutableList<InviteWithId> {
            var userInvites = mutableListOf<InviteWithId>()
            for (invite in invites) {
                if( invite.invite.target.uid == userUid ){
                    Log.d(TAG, "INVITE from ${invite.invite.source.username}")
                    userInvites.add(invite)
                }
            }
            return userInvites
        }

        fun getInvites(dataSnapshot: DataSnapshot) : MutableList<InviteWithId>{
            var invites = mutableListOf<InviteWithId>()

            for (ds in dataSnapshot.children) {
                Log.d(TAG,"Invite ID: ${ds.key.toString()}")

                val uidSource =  ds.child("source").child("uid").value.toString()
                val usernameSource = ds.child("source").child("username").value.toString()
                val source = PartialUser(usernameSource,uidSource)
                val uidTarget = ds.child("target").child("uid").value.toString()
                val usernameTarget = ds.child("target").child("username").value.toString()
                val target = PartialUser(usernameTarget, uidTarget)
                val currentInvite = Invite(source, target)
                invites.add( InviteWithId(currentInvite, ds.key.toString()))
            }
            return invites
        }
    }
}


class DeleteInvite {
    companion object {

        private const val TAG = "DeleteInvite"
        private var rootRef : DatabaseReference

        init {
            rootRef = FirebaseDatabase.
            getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference
        }

        fun deleteInvite(inviteId : String) {
            val inviteReference = rootRef.child("Invites").child(inviteId);
            inviteReference.removeValue();
        }
    }
}


class RequestAcceptor(val source : PartialUser) : Transaction.Handler {

    override fun doTransaction(currentData: MutableData): Transaction.Result {
        val ls = currentData.value as ArrayList<MutableMap<String,Any>>?

        if(ls == null){
            return Transaction.success(currentData)
        }else{
            val partialUserMap = HashMap<String,Any>()
            partialUserMap["uid"] = source.uid
            partialUserMap["username"] = source.username
            ls.add(partialUserMap)
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


class FriendDeleter(val toRemove : PartialUser) : Transaction.Handler {
    private val TAG = "FriendDeleter"

    override fun doTransaction(currentData: MutableData): Transaction.Result {
        val ls = currentData.value as ArrayList<MutableMap<String,Any>>?

        if(ls == null){
            return Transaction.success(currentData)
        }else{
            var indexOfUserToRemove = -1
            for((index, user) in ls.withIndex()){
                Log.d(TAG, "for index $index got ${user["uid"]}")
                if(user["uid"] == toRemove.uid){
                    indexOfUserToRemove = index
                }
            }
            if( indexOfUserToRemove == -1){
                Log.d(TAG, "did not find ${toRemove.username} ")
            }
            else {
                Log.d(TAG, "FOUND ${toRemove.username} at index $indexOfUserToRemove")
                ls.removeAt(indexOfUserToRemove)
            }
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



