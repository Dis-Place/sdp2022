package com.github.displace.sdp2022.profile.messages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.MessageUpdater
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.CheckConnection.checkForInternet

class SendMessageActivity : AppCompatActivity() {

    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var receiverMessage: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        receiverId = intent.getStringExtra("MessageReceiverID").toString()
        receiverName = intent.getStringExtra("MessageReceiverName").toString()
        findViewById<TextView>(R.id.receiverName).text = "Message to : $receiverName"

    }


    @Suppress("UNUSED_PARAMETER")
    fun sendMessage(view: View) {
        if(!checkForInternet(this)) {
            Toast.makeText(this, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
        } else {
            val message: String = findViewById<EditText>(R.id.messageToSend).text.toString()
            val app = applicationContext as MyApplication

            val db: RealTimeDatabase = RealTimeDatabase().noCacheInstantiate(
                "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
                false
            ) as RealTimeDatabase
            val activeUser = app.getActiveUser()
            var activePartialUser = PartialUser("defaultName", "dummy_id")
            if (activeUser != null) {
                activePartialUser = activeUser.getPartialUser()
            }

            db.getDbReference("CompleteUsers/$receiverId/MessageHistory").runTransaction(
                MessageUpdater(applicationContext, message, activePartialUser)
            )
        }

        val intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intent)
    }



}