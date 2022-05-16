package com.github.displace.sdp2022.profile

import android.Manifest
import android.content.Context
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
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.friendInvites.AddFriendActivity
import com.github.displace.sdp2022.profile.friendInvites.FriendRequestViewAdapter
import com.github.displace.sdp2022.profile.friendInvites.InviteWithId
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.history.HistoryViewAdapter
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.messages.MsgViewAdapter
import com.github.displace.sdp2022.profile.qrcode.QrCodeScannerActivity
import com.github.displace.sdp2022.profile.qrcode.QrCodeUtils
import com.github.displace.sdp2022.profile.settings.AccountSettingsActivity
import com.github.displace.sdp2022.profile.statistics.StatViewAdapter
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.CompleteUser
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
    private lateinit var activeUser: CompleteUser
    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* Active user Information */
        setUserInfo()


        /* Show status */
        setStatus(activeUser != null && !activeUser!!.offlineMode)

        /* Achievements, Statistics and Game History */
        setDefaultRecycler<Achievement,AchViewAdapter>(R.id.recyclerAch,activeUser!!.getAchievements().reversed() )
        setDefaultRecycler<Statistic,StatViewAdapter>(R.id.recyclerStats,activeUser!!.getStats().reversed() )
        setDefaultRecycler<History,HistoryViewAdapter>(R.id.recyclerHist,activeUser!!.getGameHistory().reversed() )

        /* Friends */
        setFriends()

        /* Messages */
        setMessages()

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

    /**
     *
     */
    private fun setUserInfo() {
        app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!
        activePartialUser = activeUser.getPartialUser()
        findViewById<TextView>(R.id.profileUsername).text = activePartialUser.username
    }

    /**
     *
     */
    private fun setMessages() {

        app.getMessageHandler().checkForNewMessages()

        if(activeUser != null && activeUser!!.offlineMode) {
            updateMessageListView(activeUser!!.getMessageHistory())
        } else {

            db.referenceGet("CompleteUsers/" + activePartialUser.uid, "MessageHistory")
                .addOnSuccessListener { msg ->
                    val ls = msg.value as ArrayList<HashMap<String, Any>>?
                    updateMessageListView(fromDBToMsgList(ls))
                }
            db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").addValueEventListener(messageListener())
        }
    }

    /**
     *
     */
    private fun setFriends() {
        updateFriendListView()
        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/friendsList").addValueEventListener(friendListListener())
    }

    /**
     * Sets up the recycler view of type U with data of type T
     * @param UiId : the ui element to update
     * @param data : the data used to update that ui element
     */
    private inline fun <T,reified U : RecyclerView.Adapter<*>> setDefaultRecycler(UiId : Int, data : List<T> ) {
        val recyclerView = findViewById<RecyclerView>(UiId)

        val adapter =
            U::class.constructors.first().call(applicationContext, data)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    /**
     *
     */
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


    /**
     * How the UI must be at the beginning of the activity
     */
    private fun activityStart() {
        changeUi(R.id.ProfileScroll)
    }

    @Suppress("UNUSED_PARAMETER")
    fun profileButton(view: View) {
        activityStart()
    }

    @Suppress("UNUSED_PARAMETER")
    fun inboxButton(view: View) {
        changeUi(R.id.InboxScroll)
    }

    @Suppress("UNUSED_PARAMETER")
    fun friendsButton(view: View) {
        changeUi(R.id.FriendsScroll)
    }

    /**
     * Only make the view with the given id visible
     * @param toView : the id of the view that has to be visible
     */
    private fun changeUi(toView : Int){
        val ids = arrayOf(R.id.ProfileScroll,R.id.InboxScroll,R.id.FriendsScroll)

        ids.map{ id ->
            if(id == toView) {
                findViewById<ScrollView>(id).visibility = View.VISIBLE
            }else{
                findViewById<ScrollView>(id).visibility = View.GONE
            }
        }

    }


    @Suppress("UNUSED_PARAMETER")
    fun settingsButton(view: View) {
        if(activeUser.offlineMode || !checkForInternet(this)) {
            Toast.makeText(this, "You're offline ! Please connect to the internet", Toast.LENGTH_LONG).show()
        } else if(activeUser.guestBoolean) {
            Toast.makeText(this, "You're in guest mode !", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(this, "The QR code could not be created, try again later",
                Toast.LENGTH_LONG).show()
        }

    }

    /**
     * Transition to the QR scanner activity : allows to scan another users' code and send them a friend invite
     */
    private fun launchQRScanner(){
        val intent = Intent(this, QrCodeScannerActivity::class.java)
        startActivity(intent)
    }


    /**
     * Checks if the permissions are correct before making the transition to the scanner
     */
    @Suppress("UNUSED_PARAMETER")
    fun useScannerQR(view : View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
            launchQRScanner()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), QR_CAMERA_REQUEST_CODE)
        }
    }

    /**
     * Check if the permissions have been correctly granted. If not do not transition to the scanner
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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


    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.profileUsername).text = activeUser.getPartialUser().username

        app.getMessageHandler().checkForNewMessages()
    }


}