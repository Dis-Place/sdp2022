package com.github.displace.sdp2022.profile

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.AchViewAdapter
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.friendInvites.AddFriendActivity
import com.github.displace.sdp2022.profile.friendInvites.FriendRequestViewAdapter
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.profile.qrcode.QrCodeScannerActivity
import com.github.displace.sdp2022.profile.qrcode.QrCodeUtils
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.CheckConnection.checkForInternet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileActivity : AppCompatActivity() {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    private lateinit var msgLs: ArrayList<HashMap<String, Any>>

    private lateinit var activePartialUser: PartialUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Active user Information */
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        findViewById<TextView>(R.id.profileUsername).text =
            activeUser?.getPartialUser()?.username ?: "defaultNotLoggedIn"
        var activePartialUser = PartialUser("defaultName","dummy_id")
        if(activeUser != null){
            activePartialUser = activeUser.getPartialUser()
        }


        /* Show status */
        setStatus(activeUser != null && !activeUser.offlineMode)

        /* Achievements */ //Should add a listener to it
        val achRecyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val achs = activeUser?.getAchievements() ?: mutableListOf()

        val achAdapter =
            AchViewAdapter(applicationContext, achs.reversed())
        achRecyclerView.adapter = achAdapter
        achRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Statistics */ //Should add a listener to it
        val statRecyclerView = findViewById<RecyclerView>(R.id.recyclerStats)

        val stats = activeUser?.getStats() ?: mutableListOf()

        val statAdapter =
            StatViewAdapter(applicationContext, stats)
        statRecyclerView.adapter = statAdapter
        statRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Games History */ //Should add a listener to it
        val historyRecyclerView = findViewById<RecyclerView>(R.id.recyclerHist)

        val hist = activeUser?.getGameHistory() ?: mutableListOf()

        val historyAdapter =
            HistoryViewAdapter(applicationContext, hist.reversed())
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /* Friends */
        updateFriendListView()

        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/friendsList").addValueEventListener(friendListListener())

        /* Messages */


        if(activeUser != null && activeUser.offlineMode) {
            updateMessageListView(activeUser.getMessageHistory())
        } else {

            db.referenceGet("CompleteUsers/" + activePartialUser.uid, "MessageHistory")
                .addOnSuccessListener { msg ->
                    val ls = msg.value as ArrayList<HashMap<String, Any>>?
                    updateMessageListView(fromDBToMsgList(ls))
                }
            db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").addValueEventListener(messageListener())
        }

        /*Set the default at the start*/
        activityStart()


        val rootRef = FirebaseDatabase.
        getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference
        val currentUser = activePartialUser // activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")

        val recyclerview = findViewById<RecyclerView>(R.id.friendRequestRecyclerView)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = FriendRequestViewAdapter( mutableListOf<InviteWithId>(), this)

        ReceiveFriendRequests.receiveRequests(rootRef, currentUser)
            .observe(this,  Observer{
                val adapter = FriendRequestViewAdapter(it, this)

                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

            })


    }

    fun setStatus(online: Boolean) {
        val onlineLight = findViewById<ImageView>(R.id.onlineStatus)
        val offlineLight = findViewById<ImageView>(R.id.offlineStatus)
        if(online) {
            onlineLight.visibility = View.VISIBLE
            offlineLight.visibility = View.INVISIBLE
        } else {
            onlineLight.visibility = View.INVISIBLE
            offlineLight.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        findViewById<TextView>(R.id.profileUsername).text =
            activeUser?.getPartialUser()?.username ?: "defaultNotLoggedIn"
    }

    private fun activityStart() {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    @Suppress("UNUSED_PARAMETER")
    fun profileButton(view: View) {
        activityStart()
    }

    @Suppress("UNUSED_PARAMETER")
    fun inboxButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
    }

    @Suppress("UNUSED_PARAMETER")
    fun friendsButton(view: View) {
        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.VISIBLE
    }

    @Suppress("UNUSED_PARAMETER")
    fun settingsButton(view: View) {
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()

        if(activeUser != null) {
            if(activeUser.offlineMode || !checkForInternet(this)) {
                Toast.makeText(this, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
            } else if(activeUser.guestBoolean) {
                Toast.makeText(this, "You're in guest mode !", Toast.LENGTH_LONG).show()
            } else {

                val intent = Intent(this, AccountSettingsActivity::class.java)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "huh", Toast.LENGTH_LONG).show()
        }

    }

    private fun messageListener() = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            updateMessageListView(fromDBToMsgList(ls))
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private fun fromDBToMsgList(ls : ArrayList<HashMap<String,Any>>?): ArrayList<Message> {
        var list = arrayListOf<Message>()
        if(ls != null){
            list = (applicationContext as MyApplication).getMessageHandler().getListOfMessages(ls)
            (applicationContext as MyApplication).getActiveUser()?.cacheMessages(list)
        }
        return list
    }

    private fun updateMessageListView(list: ArrayList<Message>){
        val messageRecyclerView = findViewById<RecyclerView>(R.id.recyclerMsg)

        val messageAdapter = MsgViewAdapter(
            applicationContext,
            list,
            0
        )
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        /**
         * Check for achievements on messages
         */
        val app = applicationContext as MyApplication
        val user = app.getActiveUser()!!

        AchievementsLibrary.achievementCheck(user,list.size.toLong(),AchievementsLibrary.messageLib)

    }

    //if the adding friend uses the local list
    private fun friendListListener() = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            updateFriendListView()
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }
    private fun updateFriendListView(){
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()

        val friends = activeUser?.getFriendsList() ?: mutableListOf()
        val friendAdapter = FriendViewAdapter(
            applicationContext,
            friends.reversed(),
            0
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        //Check for achievements
        AchievementsLibrary.achievementCheck(activeUser!!,friends.size.toLong(),AchievementsLibrary.friendLib)

    }


    @Suppress("UNUSED_PARAMETER")
    fun addFriendButton(view: View) {
        if(checkForInternet(this)) {
            startActivity(Intent(this, AddFriendActivity::class.java))
        } else {
            setStatus(false)

            Toast.makeText(this, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Show the QR code corresponding to the partial user after generating the bitmap
     */
    @Suppress("UNUSED_PARAMETER")
    fun showQR(view : View){
        val app = applicationContext as MyApplication
        val bmp = QrCodeUtils.generateQrCodeBitmap(app.getActiveUser()!!.getPartialUser())
        if(bmp != null){
            QrCodeUtils.createImagePopup(bmp,this)
        }else{
            TODO("There was an error while creating the bitmap, no idea what we can do here")
        }

    }

    private fun launchQRScanner(){
        val intent = Intent(this, QrCodeScannerActivity::class.java)
        startActivity(intent)
    }


    /**
     * Transition to the scanning activity
     */
    @Suppress("UNUSED_PARAMETER")
    fun useScannerQR(view : View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
            launchQRScanner()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), QR_CAMERA_REQUEST_CODE)
        }
    }

    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            QR_CAMERA_REQUEST_CODE ->
                if(grantResults[0]==PermissionChecker.PERMISSION_GRANTED) {
                    launchQRScanner()
                } else {
                    Toast.makeText(this, "Please grant Camera permissions in order to scan QR codes", Toast.LENGTH_SHORT).show()
                }
        }


    }

    companion object {
        const val QR_CAMERA_REQUEST_CODE = 1256
    }


}