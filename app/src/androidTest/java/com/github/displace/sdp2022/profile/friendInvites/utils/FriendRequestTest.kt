package com.github.displace.sdp2022.profile.friendInvites.utils

import android.provider.ContactsContract
import android.util.Log
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when`


//https://semaphoreci.com/community/tutorials/stubbing-and-mocking-with-mockito-2-and-junit
@RunWith(AndroidJUnit4::class)
class FriendRequestTest {

    @Test
    fun sendFriendRequestTest(){
//        fun sendFriendRequest(context : Context,target: String,rootRef: DatabaseReferencecurrentUser: PartialUser)

        //        MockitoAnnotations.initMocks(DataSnapshot::class.java)
//        val mockedDataSnapshot: DataSnapshot = Mockito.mock(DataSnapshot::class.java)
//        doReturn("Test").when(mockedDataSnapshot.children)

        val mockDataSnapshot : DataSnapshot = mock(DataSnapshot::class.java)
        val mockIterator = mock(Iterator::class.java)

//        `when`(mockDataSnapshot.children).thenReturn("test")
    }

    @Test
    fun sendInviteTest(){
//        sendInvite(source : PartialUser, target : PartialUser)
    }

    @Test
    fun checkUserExistsCheck(){
//        fun checkUserExists(users: List<PartialUser>, target: String)
        val partialUsers : List<PartialUser> = listOf<PartialUser>(
            PartialUser("dude", "5"),  PartialUser("dudette", "4") )
        val target = "dudette"
        assertTrue(FriendRequest.checkUserExists(partialUsers,target))
    }

    @Test
    fun getPartialUsersTest(){
//        fun getPartialUsers(dataSnapshot: DataSnapshot) : MutableList<PartialUser>
//        val dataSnapshot = DataSnapshot( )
    }

    @Test
    fun getTargetTest(){
//        fun getTargetUser(users : List<PartialUser>, target : String) : PartialUser
        val target : PartialUser = PartialUser("testname", "testuid")
        val partialUsers : List<PartialUser> = listOf<PartialUser>(
            PartialUser("dude", "5"),  PartialUser("dudette", "4"),  PartialUser("testname", "testuid"))
        assertEquals(target ,  FriendRequest.getTargetUser(partialUsers, target.username))


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
