package com.github.displace.sdp2022.profile.friendInvites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.ThemeManager
import com.google.firebase.database.*


private const val TAG = "AddFriendActivity"

class AddFriendActivity : AppCompatActivity() {


    private lateinit var rootRef: DatabaseReference
    private lateinit var currentUser : PartialUser
    private var currentUserFriends : List<PartialUser> = listOf<PartialUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        Log.d(TAG, "Entering Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        rootRef = FirebaseDatabase.
            getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").
            reference

        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()


        if (activeUser != null) {
            currentUserFriends = activeUser.getFriendsList()
        }
        currentUser = activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")
    }

    fun sendFriendRequest(view: View) {
        closeKeyBoard()
        val editText = findViewById<View>(R.id.friendRequestEditText) as EditText


        val target = editText.text.toString()

        // to check for guests
        val regex = """guest_*""".toRegex()

        when {
            regex.containsMatchIn(currentUser.uid) -> {
                Toast.makeText(this , "Guest account cannot add friends", Toast.LENGTH_LONG).show()
            }

            alreadyfriends(currentUserFriends, target) ->{
                Toast.makeText(this , "Good news: already friends with $target", Toast.LENGTH_LONG).show()
            }
            target == currentUser.username -> {
                Toast.makeText(this , "Cannot be your own friend", Toast.LENGTH_LONG).show()
            }
            regex.containsMatchIn(target) -> Toast.makeText(this , "Cannot add a guest as friend", Toast.LENGTH_LONG).show()
            else -> {
                FriendRequest.sendFriendRequest(this, target, rootRef, currentUser)
            }
        }









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

    fun alreadyfriends(friends: List<PartialUser>, target  : String) : Boolean{
        for( friend in friends){
            if( friend.username == target){
                return true
            }
        }
        return false
    }

}