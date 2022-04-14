package com.github.displace.sdp2022.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.R
import kotlinx.serialization.descriptors.PrimitiveKind


class AddFriendActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
    }

    fun sendFriendRequest(view: View) {
        closeKeyBoard()
        val editText = findViewById<View>(R.id.friendRequestEditText) as EditText
        val friendId = editText.text.toString()
        Toast.makeText(this , friendId, Toast.LENGTH_LONG).show()

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
        //TODO: implement
    }

    private fun sendInvite(){
        //TODO: implement
    }
}