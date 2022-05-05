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

}