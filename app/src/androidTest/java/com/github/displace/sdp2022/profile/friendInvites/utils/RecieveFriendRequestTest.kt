package com.github.displace.sdp2022.profile.friendInvites.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.RecieveFriendRequests.Companion.getUserInvites
import com.github.displace.sdp2022.profile.friendInvites.Invite
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

class mockDataSnapshot : DataSnapshot {


}
@RunWith(AndroidJUnit4::class)
class RecieveFriendRequestTest {

    @Test
    fun getUserInvitesTest() {
        val userUid = "asdf"
        val invites = mutableListOf<InviteWithId>(
            InviteWithId(Invite(PartialUser("a", "test"), (PartialUser("b", "asdf"))), "5"),
            InviteWithId(Invite(PartialUser("c", "test"), (PartialUser("d", "fdsdf"))), "4")
        )
        val expected = mutableListOf<InviteWithId>(
            InviteWithId(Invite(PartialUser("a", "test"), (PartialUser("b", "asdf"))), "5")
        )
        assertEquals(expected, getUserInvites(invites, userUid))
    }

//    fun recieveRequests(rootRef: DatabaseReference, currentUser : PartialUser) : MutableLiveData<MutableList<InviteWithId>> {

    @Test
    fun getInvitesTest(){

//    fun getInvites(dataSnapshot: DataSnapshot): MutableList<InviteWithId> {

    }
}
//class RecieveFriendRequests {
//    companion object {
//
//        private var invitesLiveData = MutableLiveData<MutableList<InviteWithId>>()
//        private const val TAG = "RecieveFriendRequests"
//        private lateinit var rootRef : DatabaseReference
//        // target is the user name
//
//        fun recieveRequests(rootRef: DatabaseReference, currentUser : PartialUser) : MutableLiveData<MutableList<InviteWithId>> {
//            var currentUserInvites = mutableListOf<InviteWithId>()
//            this.rootRef = rootRef
//
//            Log.d(TAG,"CHECK IF USER ${currentUser.username.toString()} has friend requests")
//            val inviteRef: DatabaseReference = rootRef.child("Invites")
//
//            val eventListener: ValueEventListener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot){
//                    var invites = getInvites(dataSnapshot)
//                    currentUserInvites = getUserInvites(invites, currentUser.uid)
//                    invitesLiveData.value = currentUserInvites
//                }
//                override fun onCancelled(databaseError: DatabaseError) {}
//            }
//            inviteRef.addListenerForSingleValueEvent(eventListener)
//            return invitesLiveData
//
//        }
//
//        fun getUserInvites(invites : MutableList<InviteWithId>, userUid : String) : MutableList<InviteWithId> {
//            var userInvites = mutableListOf<InviteWithId>()
//            for (invite in invites) {
//                if( invite.invite.target.uid == userUid ){
//                    Log.d(TAG, "INVITE from ${invite.invite.source.username}")
//                    userInvites.add(invite)
//                }
//            }
//            return userInvites
//        }
//
//        fun getInvites(dataSnapshot: DataSnapshot) : MutableList<InviteWithId>{
//            var invites = mutableListOf<InviteWithId>()
//
//            for (ds in dataSnapshot.children) {
//                Log.d(TAG,"Invite ID: ${ds.key.toString()}")
//
//                val uidSource =  ds.child("source").child("uid").value.toString()
//                val usernameSource = ds.child("source").child("username").value.toString()
//                val source = PartialUser(usernameSource,uidSource)
//                val uidTarget = ds.child("target").child("uid").value.toString()
//                val usernameTarget = ds.child("target").child("username").value.toString()
//                val target = PartialUser(usernameTarget, uidTarget)
//                val currentInvite = Invite(source, target)
//                invites.add( InviteWithId(currentInvite, ds.key.toString()))
//            }
//            return invites
//        }
//    }
//}
