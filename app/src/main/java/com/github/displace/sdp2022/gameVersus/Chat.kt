package com.github.displace.sdp2022.gameVersus

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*
import com.github.displace.sdp2022.R

class Chat(val chatPath : String , val db : RealTimeDatabase , val view : View, val applicationContext : Context) {

    val chatGroup : ConstraintLayout

    init{
        db.getDbReference(chatPath).addValueEventListener(chatListener())
        chatGroup = view.findViewById<ConstraintLayout>(R.id.chatLayout)
    }

    private fun chatListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val messageRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            var list = mutableListOf<Message>()

            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            if(ls != null){
                list =(applicationContext as MyApplication).getMessageHandler().getListOfMessages(ls)
            }

            val messageAdapter = MsgViewAdapter(
                applicationContext,
                list,
                1
            )
            messageRecyclerView.adapter = messageAdapter
            messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }


    fun addToChat(){
        val msg : String = view.findViewById<EditText>(R.id.chatEditText).text.toString()
        val partialUser : PartialUser = (applicationContext as MyApplication).getActiveUser()?.getPartialUser()!!
        val date : String = (applicationContext as MyApplication).getCurrentTime()
        if(msg.isEmpty()){
            return
        }
        db.getDbReference(chatPath)
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var ls = currentData.value as ArrayList<HashMap<String,Any>>?
                    val map = HashMap<String,Any>()
                    map["message"] = msg
                    map["date"] = date
                    map["sender"] = partialUser
                    val msgLs = arrayListOf(map)
                    if(ls != null) {
                        ls.addAll(msgLs)
                        if(ls.size >= 6){
                            ls = ls.takeLast(5) as ArrayList<HashMap<String, Any>>
                        }
                        currentData.value = ls
                    }else {
                        currentData.value = msgLs
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    //  TODO("Not yet implemented")
                }

            })
    }

    fun removeListener(){
        db.getDbReference(chatPath).removeEventListener(chatListener())
    }

    fun showChat(){
        chatGroup.visibility = View.VISIBLE
    }

    fun hideChat(){
        chatGroup.visibility = View.GONE

    }

}