package com.github.displace.sdp2022.profile

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*

class MessageUpdater(val custom : Boolean,val applicationContext : Context, val message : String, val activePartialUser : PartialUser ) : Transaction.Handler {
    val app = applicationContext as MyApplication
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
        if(committed && custom){
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(applicationContext,intent,null)
        }else{
            //       TODO("ERROR MESSAGE : COULD NOT BE SENT")
        }
    }

}