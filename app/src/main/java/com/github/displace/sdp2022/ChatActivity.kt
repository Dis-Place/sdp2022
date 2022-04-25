package com.github.displace.sdp2022
/*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.Group
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
        showButton( findViewById<Group>(R.id.ChatActiveGroup))
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
        if(msg.isEmpty()){
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

    fun showChat(view : View){
        val chatGroup = findViewById<Group>(R.id.ChatActiveGroup)
        val chatButton = findViewById<Button>(R.id.button3)
        chatButton.visibility = View.INVISIBLE
        chatGroup.visibility = View.VISIBLE
    }

    fun showButton(view : View){
        val chatGroup = findViewById<Group>(R.id.ChatActiveGroup)
        val chatButton = findViewById<Button>(R.id.button3)
        chatButton.visibility = View.VISIBLE
        chatGroup.visibility = View.INVISIBLE
    }

}*/