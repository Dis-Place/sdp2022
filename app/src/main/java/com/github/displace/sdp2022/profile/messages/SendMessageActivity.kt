package com.github.displace.sdp2022.profile.messages

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.github.blecoeur.bootcamp.MyApplication
import com.github.blecoeur.bootcamp.R
import com.github.blecoeur.bootcamp.profile.ProfileActivity

class SendMessageActivity : AppCompatActivity() {

    private lateinit var receiverId : String
    private lateinit var receiverName : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        receiverId = intent.getStringExtra("MessageReceiverID").toString()
        receiverName = intent.getStringExtra("MessageReceiverName").toString()
        findViewById<TextView>(R.id.receiverName).text = "Message to : $receiverName"

    }

    fun sendMessage(view : View){
        val message : String = findViewById<EditText>(R.id.messageToSend).text.toString()

        val app = applicationContext as MyApplication
        val dbAccess = app.getProfileDb()

        dbAccess.sendMessage(message,receiverId)

        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)


    }

}