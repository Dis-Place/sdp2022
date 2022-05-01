package com.github.displace.sdp2022.profile.friendInvites.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.friendInvites.Invite
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import org.junit.Test
import org.junit.runner.RunWith

//https://semaphoreci.com/community/tutorials/stubbing-and-mocking-with-mockito-2-and-junit
@RunWith(AndroidJUnit4::class)
class DeleteFriendRequestTest {

    @Test
    fun sendFriendRequestTest(){
//        fun sendFriendRequest(context : Context,target: String,rootRef: DatabaseReferencecurrentUser: PartialUser)
    }

    @Test
    fun sendInviteTest(){
//        sendInvite(source : PartialUser, target : PartialUser)
    }

    @Test
    fun checkUserExistsCheck(){
//        fun checkUserExists(users: List<PartialUser>, target: String)
    }

    @Test
    fun getPartialUsersTest(){
//        fun getPartialUsers(dataSnapshot: DataSnapshot) : MutableList<PartialUser>
    }

    @Test
    fun getTargetTest(){
//        fun getTargetUser(dataSnapshot: DataSnapshot, users : List<PartialUser>, target : String) : PartialUser
    }

}

//class FriendRequest {
//    companion object {
//
//        private const val TAG = "FriendRequest"
//        private lateinit var rootRef : DatabaseReference
//
//        // target is the user name
//        fun sendFriendRequest(
//            context : Context,
//            target: String,
//            rootRef: DatabaseReference,
//            currentUser: PartialUser
//        ) {
//
//            this.rootRef = rootRef
//
//            Log.d(TAG, "CHECK if $target exists")
//            val usersRef: DatabaseReference = rootRef.child("CompleteUsers")
//
//            val eventListener: ValueEventListener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    var partialUsers = getPartialUsers(dataSnapshot)
//                    if (checkUserExists(partialUsers, target)) {
//                        val source = currentUser
//                        val target = getTargetUser(dataSnapshot, partialUsers, target)
//                        sendInvite(source, target)
//                    }
//                    else{
//                        Log.d(TAG, "$target DOES NOTEXISTS")
//                        Toast.makeText(context,"User $target does not exist", Toast.LENGTH_LONG).show()
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {}
//            }
//            usersRef.addListenerForSingleValueEvent(eventListener)
//
//
//        }
//
//        fun sendInvite(source : PartialUser, target : PartialUser){
//            val inviteDbRef = rootRef.child("Invites")
//            val invite = Invite(source, target)
//            inviteDbRef.push().setValue(invite)
//        }
//
//        fun checkUserExists(users: List<PartialUser>, target: String): Boolean {
//            Log.d(TAG, "Searching if $target")
//            for (user in users) {
//                if (user.username == target) {
//                    return true
//                }
//            }
//            return false
//        }
//
//        fun getPartialUsers(dataSnapshot: DataSnapshot) : MutableList<PartialUser>{
//            var partialUsers = mutableListOf<PartialUser>()
//            for (ds in dataSnapshot.children) {
//                val uid = ds.child("CompleteUser").child("partialUser").child("uid").value.toString()
//                val username = ds.child("CompleteUser").child("partialUser").child("username").value.toString()
//
//                val partialUser = PartialUser(username,uid)
////                    Log.d(TAG, partialUser.toString())
//                partialUsers.add(partialUser)
//            }
//            return partialUsers
//        }
//
//        fun getTargetUser(dataSnapshot: DataSnapshot, users : List<PartialUser>, target : String) : PartialUser {
//            for (user in users) {
//                if( user.username == target ){
//                    return user
//                }
//            }
//            return PartialUser("empty", "empty") // never gets executed as we check that the user exists before
//        }
//    }
//}
