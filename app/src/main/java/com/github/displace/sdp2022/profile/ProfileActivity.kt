package com.github.displace.sdp2022.profile

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.GoodDB
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
import com.github.displace.sdp2022.util.CheckConnectionUtil.checkForInternet
import com.github.displace.sdp2022.util.listeners.Listener
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity() {


    private lateinit var db : GoodDB

    //private lateinit var msgLs: List<Map<String, Any>>

    private lateinit var activePartialUser: PartialUser
    private lateinit var activeUser: CompleteUser
    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseFactory.getDB(intent)

        /* Active user Information */
        setUserInfo()


        /* Show status */
        setStatus(!activeUser.offlineMode)

        /* Achievements, Statistics and Game History */
        setDefaultRecycler<Achievement,AchViewAdapter>(R.id.recyclerAch,
            activeUser.getAchievements().reversed() )
        setDefaultRecycler<Statistic,StatViewAdapter>(R.id.recyclerStats, activeUser.getStats().reversed() )
        setDefaultRecycler<History,HistoryViewAdapter>(R.id.recyclerHist,
            activeUser.getGameHistory().reversed() )

        /* Friends */
        setFriends()

        /* Messages */
        setMessages()

        /*Set the default UI at the start */
        activityStart()

        /* Friend requests */
        val rootRef = FirebaseDatabase.
        getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference
        val currentUser = activePartialUser // activeUser?.getPartialUser() ?: PartialUser("dummy", "dummy")

        val recyclerview = findViewById<RecyclerView>(R.id.friendRequestRecyclerView)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = FriendRequestViewAdapter( mutableListOf<InviteWithId>(), this)

        Log.d("inbox", "curent partial $activePartialUser")
        observeInbox()



    }

    /**
     * Sets the user info for the rest activity to use
     */
    private fun setUserInfo() {
        app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!
        activePartialUser = activeUser.getPartialUser()
        findViewById<TextView>(R.id.profileUsername).text = activePartialUser.username
    }

    /**
     * Sets the correct messages of the view
     */
    private fun setMessages() {

        if(activeUser.offlineMode) {
            updateMessageListView(activeUser.getMessageHistory())
        } else {
            app.getMessageHandler().checkForNewMessages()
            updateMessageListView(activeUser.getMessageHistory())
            db.addListener<List<Map<String, Any>>?>("CompleteUsers/" + activePartialUser.uid + "/CompleteUser/MessageHistory",messageListener)
        }
    }

    /**
     * Sets the correct friends of the view : using a listener
     */
    private fun setFriends() {
        updateFriendListView()
        db.addListener("CompleteUsers/" + activePartialUser.uid + "/friendsList",friendListListener)
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
     * Sets the status of the user, online or offline
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

        findViewById<Button>(R.id.innerProfileButton).setOnClickListener {
            changeUi(R.id.ProfileScroll)
        }
        findViewById<Button>(R.id.inboxButton).setOnClickListener {
            observeInbox()
            changeUi(R.id.InboxScroll)
        }
        findViewById<Button>(R.id.friendsButton).setOnClickListener {
            changeUi(R.id.FriendsScroll)
        }


//    @Suppress("UNUSED_PARAMETER")
//    fun inboxButton(view: View) {
//        val app = applicationContext as MyApplication
//        val activeUser = app.getActiveUser()
//        var partialUser = PartialUser("defaultName","dummy_id")
//        if(activeUser != null){
//            partialUser = activeUser.getPartialUser()
//        }
//        observeInbox(partialUser)
//        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
//        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.VISIBLE
//        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
//    }
//
//    @Suppress("UNUSED_PARAMETER")
//    fun friendsButton(view: View) {
//        updateFriendListView()
//        findViewById<ScrollView>(R.id.ProfileScroll).visibility = View.GONE
//        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
//        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.VISIBLE
//=======
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

    /**
     * Launch the account settings activity
     */
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



    /**
     * Transform teh data of the database into a list of messages
     */
    private fun fromDBToMsgList(ls : List<Map<String,Any>>?): List<Message> {
        var list = listOf<Message>()
        if(ls != null){
            list = (applicationContext as MyApplication).getMessageHandler().getListOfMessages(ls)
            (applicationContext as MyApplication).getActiveUser()?.cacheMessages(list as ArrayList<Message>)
        }
        return list
    }

    /**
     * Use a list of messages to update the UI
     */
    private fun updateMessageListView(list: List<Message>){
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

    /**
     * The listener for when a new friend is added to the user
     */
    private val friendListListener = Listener<Unit?>{ updateFriendListView() }

    /**
     * Listener for when a new message is received
     */
    private val messageListener = Listener<List<Map<String, Any>>?> { value -> updateMessageListView(fromDBToMsgList(value)) }




    /**
     * Updates the UI with the new list of friends
     */
    private fun updateFriendListView(){
        val friendRecyclerView = findViewById<RecyclerView>(R.id.recyclerFriend)
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()

        val friends = activeUser?.getFriendsList() ?: listOf()
        Log.d("UpdateFriendList", "current friends $friends")
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

    /**
     * Launch the activity to add friends
     */
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

    fun observeInbox(){

        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        var currentUser = PartialUser("defaultName","dummy_id")
        if(activeUser != null){
            currentUser = activeUser.getPartialUser()
        }
        val rootRef = FirebaseDatabase.
        getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app").reference
        val recyclerview = findViewById<RecyclerView>(R.id.friendRequestRecyclerView)
        ReceiveFriendRequests.receiveRequests(rootRef, currentUser)
            .observe(this,  Observer{
                val adapter = FriendRequestViewAdapter(it, this)

                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

            })
    }

    companion object {
        const val QR_CAMERA_REQUEST_CODE = 1256
    }

    /**
     * When the activity is resumed, check if new messages have arrived, to be able to send a notification if needed
     */
    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.profileUsername).text = activeUser.getPartialUser().username

        app.getMessageHandler().checkForNewMessages()
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        db.removeListener("CompleteUsers/" + activePartialUser.uid + "/friendsList",friendListListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeListener("CompleteUsers/" + activePartialUser.uid + "/friendsList",friendListListener)
    }

}