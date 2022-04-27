package com.github.displace.sdp2022
/*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private val db : RealTimeDatabase = RealTimeDatabase().noCacheInstantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        db.getDbReference("debug/Chat").addValueEventListener(chatListener())

    }

    private fun chatListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val list = mutableListOf<Message>()

            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            if(ls != null){
                for( map in ls ){
                    val sender = map["sender"] as HashMap<String,Any>
                    val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
                    list.add(m)
                }
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


    fun addToChat(view : View){
        val msg : String = findViewById<EditText>(R.id.chatEditText).text.toString()
        val partialUser : PartialUser = (applicationContext as MyApplication).getActiveUser()?.getPartialUser()!!
        val date : String = (applicationContext as MyApplication).getCurrentTime()
        if(msg.length == 0){
            return
        }
        db.getDbReference("debug/Chat")
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var ls = currentData.value as ArrayList<HashMap<String,Any>>?
                    val map = HashMap<String,Any>()
                    map["message"] = msg
                    map["date"] = date
                    map["sender"] = partialUser
                    val msgLs = arrayListOf<HashMap<String,Any>>(map)
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

}*/