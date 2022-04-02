package com.github.displace.sdp2022.profile.messages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        val message: String = findViewById<EditText>(R.id.messageToSend).text.toString()
        val app = applicationContext as MyApplication
        val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
        val activeUser = app.getActiveUser()
        var activePartialUser = PartialUser("defaultName","dummy_id")
        if(activeUser != null){
            activePartialUser = activeUser.getPartialUser()
        }

        db.getDbReference("CompleteUsers/$receiverId/MessageHistory").runTransaction( object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val ls = currentData.value as ArrayList<MutableMap<String,Any>>?
                val msg = Message(message,app.getCurrentDate(), activePartialUser)
                if(ls == null){
                    return Transaction.success(currentData)
                }else{
                    val msgMap = HashMap<String,Any>()
                    msgMap["message"] = msg.message
                    msgMap["date"] = app.getCurrentDate()
                    msgMap["sender"] = msg.sender
                    ls.add(0,msgMap)
                }
                currentData.value = ls
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if(committed){
                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                    startActivity(intent)
                }else{
             //       TODO("ERROR MESSAGE : COULD NOT BE SENT")
                }
            }

        })

    }



}