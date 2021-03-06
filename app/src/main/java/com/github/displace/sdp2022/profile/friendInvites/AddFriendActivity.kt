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
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.FriendRequest
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.ThemeManager
import com.google.firebase.database.*


private const val TAG = "AddFriendActivity"  // tag for debugging

/**
 * Activity to be to send friend requests manually
 */
class AddFriendActivity : AppCompatActivity() {


    private lateinit var rootRef: DatabaseReference
    private lateinit var currentUser : PartialUser
    private var currentUserFriends : List<PartialUser> = listOf<PartialUser>()

    private lateinit var db : GoodDB

    /**
     * Gets the root reference to the database
     * As well as the partial user of the current user if there is one
     * @param savedInstanceState : the saved instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        Log.d(TAG, "Entering Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        db = DatabaseFactory.getDB(intent)

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

    /**
     * Reads the user input from the text view
     * checks if the input is valid
     * And sends a friend request
     * @param view : the view of the activity, will not be used
     */
    @Suppress("UNUSED_PARAMETER")
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
                FriendRequest.sendFriendRequest(this, target, db, currentUser)
//                FriendRequest.sendFriendRequest(this, target, rootRef, currentUser)
            }
        }







        editText.text.clear()
        editText.hint = "Enter Another Friend"

    }

    /**
     * Quick method to close the keyboard
     */
    fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Verify that you are not already friends with requested user
     * @param friends : current friends of active user
     * @param target : user we want to add
     * @return true if the target user is already in the friend list
     */
    fun alreadyfriends(friends: List<PartialUser>, target  : String) : Boolean{
        for( friend in friends){
            if( friend.username == target){
                return true
            }
        }
        return false
    }

}