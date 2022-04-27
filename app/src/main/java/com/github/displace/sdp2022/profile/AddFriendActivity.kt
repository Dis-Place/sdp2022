package com.github.displace.sdp2022.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


private const val TAG = "AddFriendActivity"

class AddFriendActivity : AppCompatActivity() {


    private lateinit var rootRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Entering Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        rootRef = FirebaseDatabase.
            getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").
            reference
    }

    fun sendFriendRequest(view: View) {
        closeKeyBoard()
        val editText = findViewById<View>(R.id.friendRequestEditText) as EditText
        val friendId = editText.text.toString()
        Toast.makeText(this , friendId, Toast.LENGTH_LONG).show()



        checkIfUserExists("")

        editText.text.clear()
        editText.hint = "Enter Another Friend"

    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun checkIfUserExists(userName : String) : Boolean{
        Log.d(TAG,"CHECK IF USER $userName EXISTS")


        val usersRef: DatabaseReference = rootRef.child("CompleteUsers")


        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var partialUsers = mutableListOf<PartialUser>()
                for (ds in dataSnapshot.children) {
                    val uid = ds.child("CompleteUser").child("partialUser").child("uid").value.toString()
                    val username = ds.child("CompleteUser").child("partialUser").child("username").value.toString()

                    val partialUser = PartialUser(username,uid)
                    Log.d(TAG, partialUser.toString())
                    partialUsers.add(partialUser)
                }

                val res = checkUserExists(partialUsers, userName)
                Log.d(TAG, res.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }


        usersRef.addListenerForSingleValueEvent(eventListener)


        return true
    }

    private fun sendInvite(){
        //TODO: implement
    }

    private fun checkUserExists(users : List<PartialUser>, target : String) : Boolean {
        for (user in users) {
            if( user.username == target ){
                return true
            }
        }
        return false
    }
}