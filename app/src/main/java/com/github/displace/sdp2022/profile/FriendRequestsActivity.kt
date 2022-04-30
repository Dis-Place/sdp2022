package com.github.displace.sdp2022.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.friendRequest.FriendRequestViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FriendRequestsActivity : AppCompatActivity() {

    private lateinit var rootRef: DatabaseReference
    private lateinit var currentUser : PartialUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_requests)

        rootRef = FirebaseDatabase.
        getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference

        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        currentUser = activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")

        val recyclerview = findViewById<RecyclerView>(R.id.friendRequestRecyclerView)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = FriendRequestViewAdapter( mutableListOf<InviteWithId>())

        RecieveFriendRequests.recieveRequests(rootRef, currentUser)
            .observe(this,  Observer{
                val adapter = FriendRequestViewAdapter(it)

                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

        })




    }
}
