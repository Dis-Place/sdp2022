package com.github.displace.sdp2022.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import com.github.displace.sdp2022.database.GoodDB

import com.github.displace.sdp2022.database.TransactionSpecification
import com.github.displace.sdp2022.profile.friendInvites.Invite
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.DateTimeUtil
import com.google.firebase.database.*
import java.util.*
import kotlin.random.Random

/**
 * A class that represent the transaction done while sending a message
 * It is used in the SendMessageActivity and to create invitations for private lobbies in MatchMaking
 *
 * @param message : the message content
 * @param activePartialUser : the current user, which is the sender of the message
 */
fun messageUpdater(message : String, activePartialUser : PartialUser) : TransactionSpecification<List<Map<String,Any>>> =
    TransactionSpecification.Builder<List<Map<String,Any>>> { ls ->
        var newLs = ls
        if(newLs != null){
            val msg = Message(message, DateTimeUtil.currentDate(), activePartialUser)
            newLs = listOf(msg.toMap()) + newLs
        }
        return@Builder newLs

    }.build()

class FriendRequest {
    companion object {

        private const val TAG = "FriendRequest"
        private var invitesLiveData = MutableLiveData<MutableList<InviteWithId>>()
        private lateinit var db : GoodDB

        // target is the user name
        fun sendFriendRequest(
            context : Context,
            target: String,
            db: GoodDB,
            currentUser: PartialUser
        ) {

            this.db = db

            val dbInviteReference = "Invites"
            db.getThenCall<Map<String, *>>(dbInviteReference) { invites ->
                if (invites != null) {
                    var invites = ReceiveFriendRequests.getInvites(invites)
                    invitesLiveData.value = invites

                }
            }

            val dbReferenceUsers = "CompleteUsers"
            db.getThenCall<Map<String, *>>(dbReferenceUsers) { completeUsers ->
                if (completeUsers != null){
                    var partialUsers = getPartialUsers(completeUsers)
                    Log.d(TAG, "users $partialUsers")
                    if( checkUserExists(partialUsers, target)){
                        Log.d(TAG, "target $target exists")
                        val source = currentUser
                        val target = getTargetUser( partialUsers, target)
                        Log.d(TAG, "live data check  ${invitesLiveData.value}")
                        if( alreadyInvited(invitesLiveData, source, target)){
                            Toast.makeText(context,"Already invited ${target.username} ", Toast.LENGTH_LONG).show()
                        }
                        else{
                            sendInvite(invitesLiveData.value, source, target)
                            Toast.makeText(context,"Invite to ${target.username} sent", Toast.LENGTH_LONG).show()

                            }
                        }
                    else{
                        Log.d(TAG, "$target DOES NOTEXISTS")
                        Toast.makeText(context,"User $target does not exist", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        fun getPartialUsers(completeUsers: Map<String, *>) : MutableList<PartialUser>{
            var partialUsers = mutableListOf<PartialUser>()
            for (user  in completeUsers.keys ) {

                val aCompleteUser = completeUsers[user] as Map<String, *>

                val ownCompleteUser = aCompleteUser["CompleteUser"] as Map<String, *>
                Log.d(TAG, "Complete user $ownCompleteUser")
                if( user != "dummy_id"){
                    val almostPartialUser = ownCompleteUser["partialUser"] as Map<String, String>
                    val partialUser = PartialUser( almostPartialUser["username"]!!, almostPartialUser["uid"]!! )
                    partialUsers.add(partialUser)
                }
            }
            return partialUsers
        }

        fun sendInvite( invites : MutableList<InviteWithId>?, source : PartialUser, target : PartialUser){

            val id = Random.nextLong(-10000000000L, 10000000000L).toString()

            val invitewithId = InviteWithId( Invite(source, target), id )

            /* invites!!.add(invitewithId)
             var newInvitesMap = mapOf<String,Any>()

             for(inv in invites){
                 newInvitesMap = inv.toMap()
             }
 */
            db.update("Invites/$id", invitewithId.invite.toMap() )

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
            if(invitesWithId.value == null){
                return false
            }
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
//                    var invites = getInvites(dataSnapshot)
//                    currentUserInvites = getUserInvites(invites, currentUser.uid)
//                    invitesLiveData.value = currentUserInvites
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

        fun getInvites(invitesMap : Map<String, *>) : MutableList<InviteWithId>{
            var invites = mutableListOf<InviteWithId>()

            for (inviteKey in invitesMap.keys) {
                val inviteMap = invitesMap[inviteKey] as Map<String, *>
                val sourceMap = inviteMap["source"] as Map<String, String>
                val targetMap = inviteMap["target"] as Map<String, String>

                val source = PartialUser(sourceMap["username"]!!, sourceMap["uid"]!! )
                val target = PartialUser(targetMap["username"]!!, targetMap["uid"]!! )
                val currentInvite = Invite(source, target)
                invites.add( InviteWithId(currentInvite, inviteKey ) )
                }

//
//                val temp = invite as Map<String,*>
//                val sourceMap = temp["source"] as Map<String,PartialUser>
//                val targetMap = temp["target"] as Map<String,PartialUser>
////                Log.d(TAG,"Invite ID: ${invite.keys}")
//
//
//
////                val uidSource =  ds.child("source").child("uid").value.toString()
////                val usernameSource = ds.child("source").child("username").value.toString()
//
//
//                val source = PartialUser(invite["source"][],uidSource)
//
//                val uidTarget = ds.child("target").child("uid").value.toString()
//                val usernameTarget = ds.child("target").child("username").value.toString()
//                val target = PartialUser(usernameTarget, uidTarget)
//                val currentInvite = Invite(source, target)
//                invites.add( InviteWithId(currentInvite, temp.keys.toList()[0]))
//            }
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


fun requestAcceptor( source : PartialUser) : TransactionSpecification<List<Map<String,Any>>> =
    TransactionSpecification.Builder<List<Map<String,Any>>> { ls ->
        if(ls == null){
            return@Builder ls
        }
        var newList = ls

        newList = newList + source.toMap()

        return@Builder newList
    }.build()

/*

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
*/

fun friendDeleter(toRemove : PartialUser) : TransactionSpecification<List<Map<String,Any>>> =
    TransactionSpecification.Builder<List<Map<String,Any>>> { ls ->
        if(ls == null){
            return@Builder ls
        }
        var newList = ls

        newList = newList - toRemove.toMap()

        return@Builder newList
    }.build()


  /*  private val TAG = "FriendDeleter"

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

}*/



