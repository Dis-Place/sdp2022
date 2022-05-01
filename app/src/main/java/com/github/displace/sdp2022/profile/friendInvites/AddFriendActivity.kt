package com.github.displace.sdp2022.profile.friendInvites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*


private const val TAG = "AddFriendActivity"

class AddFriendActivity : AppCompatActivity() {


    private lateinit var rootRef: DatabaseReference
    private lateinit var currentUser : PartialUser

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Entering Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        rootRef = FirebaseDatabase.
            getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").
            reference

        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()

        currentUser = activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")
    }

    fun sendFriendRequest(view: View) {
        closeKeyBoard()
        val editText = findViewById<View>(R.id.friendRequestEditText) as EditText
        val friendId = editText.text.toString()
//        Toast.makeText(this , friendId, Toast.LENGTH_LONG).show()

        val target = editText.text.toString()

        FriendRequest.sendFriendRequest(this, target, rootRef, currentUser)

//        Log.d(TAG,"CHECK IF USER $target EXISTS")
//        val usersRef: DatabaseReference = rootRef.child("CompleteUsers")
//
//        val eventListener: ValueEventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot){
//                var partialUsers = getPartialUsers(dataSnapshot)
//                if( checkUserExists(partialUsers, target) ){
//                    val source = currentUser
//                    val target = getTargetUser(dataSnapshot, partialUsers, target)
//                    sendInvite(source, target)
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {}
//        }
//        usersRef.addListenerForSingleValueEvent(eventListener)

        editText.text.clear()
        editText.hint = "Enter Another Friend"

    }

    fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

//    fun checkIfUserExists(target : String) : Boolean{
//        Log.d(TAG,"CHECK IF USER $target EXISTS")
//        val usersRef: DatabaseReference = rootRef.child("CompleteUsers")
//
//        val eventListener: ValueEventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot){
//                var partialUsers = getPartialUsers(dataSnapshot)
//                if( checkUserExists(partialUsers, target) ){
//                    val source = currentUser
//                    val target = getTargetUser(dataSnapshot, partialUsers, target)
//                    sendInvite(source, target)
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {}
//        }
//        usersRef.addListenerForSingleValueEvent(eventListener)
//
//
//        return true
//    }

//    fun sendInvite(source : PartialUser, target : PartialUser){
//        val inviteDbRef = rootRef.child("Invites")
//        val invite = Invite(source, target)
//        inviteDbRef.push().setValue(invite)
//    }
//
//    fun checkUserExists(users : List<PartialUser>, target : String) : Boolean {
//        Log.d(TAG, "Searching if $target")
//        for (user in users) {
//            if( user.username == target ){
//                return true
//            }
//        }
//        return false
//    }
//
//    fun getPartialUsers(dataSnapshot: DataSnapshot) : MutableList<PartialUser>{
//        var partialUsers = mutableListOf<PartialUser>()
//        for (ds in dataSnapshot.children) {
//            val uid = ds.child("CompleteUser").child("partialUser").child("uid").value.toString()
//            val username = ds.child("CompleteUser").child("partialUser").child("username").value.toString()
//
//            val partialUser = PartialUser(username,uid)
////                    Log.d(TAG, partialUser.toString())
//            partialUsers.add(partialUser)
//        }
//        return partialUsers
//    }
//
//    fun getTargetUser(dataSnapshot: DataSnapshot, users : List<PartialUser>, target : String) : PartialUser {
//        for (user in users) {
//            if( user.username == target ){
//                return user
//            }
//        }
//        return PartialUser("empty", "empty") // never gets executed as we check that the user exists before
//    }




}