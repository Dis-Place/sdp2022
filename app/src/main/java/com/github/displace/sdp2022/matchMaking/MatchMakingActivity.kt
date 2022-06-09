@file:Suppress("UNCHECKED_CAST")

package com.github.displace.sdp2022.matchMaking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.GameListActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.friends.FriendViewAdapter
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.ProgressDialogsUtil
import com.github.displace.sdp2022.util.ThemeManager

/**
 * make matches with other players using the Database (DB)
 * DB structure :
 * -> MM
 *      -> gamemode
 *          -> map (will be removed afterwards)
 *              -> type (private or public)
 *                   -> freeList : list of lobby IDs, only those that should be found
 *                   -> freeLobbies : set of lobbies, only those that should be found
 *                      -> lobbyID
 *                          -> lobby data
 *                   -> launchLobbies   : set of lobbies, those who should not be found anymore and are launching into the game
 *                      -> lobbyID
 *                          -> lobby data
 */
@Suppress("UNUSED_PARAMETER")
class MatchMakingActivity : MMView() {

    private lateinit var model : MatchMakingModel


    /**
     * On creation of the activity we must :
     * - setup the friend list for a potential private lobby : the 1 indicates that there are no message buttons
     * - show the right group of UI elements : the ones for setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyChosenTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_making) //UI

        model = MatchMakingModel(this)

        ProgressDialogsUtil.showProgressDialog(this)


        setFriendList(model.activeUser)
        uiToSetup()

        findViewById<Button>(R.id.privateLobbyCreate).setOnClickListener {
            ProgressDialogsUtil.showProgressDialog(this)
            model.createPrivateLobby()
        }
        findViewById<Button>(R.id.privateLobbyJoin).setOnClickListener {
            ProgressDialogsUtil.showProgressDialog(this)
            model.joinPrivateLobby()
        }




    }

    /**
     * Sets up the list of friends used to be able to invite them to a private lobby
     * @param activeUser : the active user, which is the source of the friends
     */
     override fun setFriendList(activeUser : CompleteUser){
        /* Friends in private lobby */
        val friendRecyclerView = findViewById<RecyclerView>(R.id.friendsMMRecycler)
        val friends = activeUser.getFriendsList()

        val friendAdapter = FriendViewAdapter(
            applicationContext,
            friends,
            1
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

    }


    override fun checkNonEmpty() : String {
        model.lobbyType = "private"
        val id = findViewById<EditText>(R.id.lobbyIdInsert).text.toString()
        if (id.isEmpty()) { //UI  //the ID can not be empty
            changeVisibility<TextView>(R.id.errorIdNonEmpty, View.VISIBLE)
        }
        return id
    }

    /**
     * Update the UI of the lobby search
     * - updates the list of players in the lobby
     */
    override fun updateUI() {
        if(model.debug){
            return
        }
        val friendRecyclerView = findViewById<RecyclerView>(R.id.playersRecycler)
        val friendAdapter = FriendViewAdapter(  //reuses the friend list format without any buttons
            applicationContext,
            (model.lobbyMap["lobbyPlayers"] as List<Map<String, Any>>).map { p ->
                PartialUser(p["username"] as String, p["uid"] as String)
            },
            2
        )
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    /**
     * Checks if the Group (UI elements) is visible
     * @param id : id of the group
     * @return true if the given group is visible
     */
    override fun isGroupVisible(id: Int): Boolean { //UI
        return findViewById<Group>(id).visibility == View.VISIBLE
    }

    /**
     * Change the visibility of the UI element
     * @param T : the type of the UI element
     * @param id : id of the UI element
     * @param visibility : VISIBLE / INVISIBLE / GONE
     */
    override fun <T : View> changeVisibility(id: Int, visibility: Int) { //UI
        findViewById<T>(id).visibility = visibility
    }

    /**
     * When the activity is destroyed, leave the current lobby
     */
    override fun onDestroy() { //UI
        model.leaveMM(false)
        ProgressDialogsUtil.dismissProgressDialog()
        super.onDestroy()
    }

    /**
     * When the activity is paused, leave the current lobby
     */
    override fun onPause() { //UI
        model.leaveMM(false)
        uiToSetup()
        super.onPause()
    }

    /**
     * When the user cancels the search, leave the current lobby and return to the setup screen
     */
    fun onCancelButton(v: View) { //UI
        ProgressDialogsUtil.showProgressDialog(this)
        model.leaveMM(false)
        uiToSetup()
    }

    /**
     * When the user starts the search
     */
    fun onPublicLobbySearchButton(v: View) {
        ProgressDialogsUtil.showProgressDialog(this)
        model.lobbyType = "public"
        model.publicSearch()
    }

    /**
     * UI transition between multiple groups of UI elements : done to keep the same activity
     * Goes from Search to Setup
     */
    override fun uiToSetup() { //UI
        changeVisibility<Group>(R.id.setupGroup, View.VISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.INVISIBLE)
        if (model.lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.INVISIBLE)
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()

        ProgressDialogsUtil.dismissProgressDialog()
    }

    /**
     * UI transition between multiple groups of UI elements : done to keep the same activity
     * Goes from Setup to Search
     */
    override fun uiToSearch() { //UI
        changeVisibility<Group>(R.id.setupGroup, View.INVISIBLE)
        changeVisibility<Group>(R.id.waitGroup, View.VISIBLE)
        if (model.lobbyType == "private") {
            changeVisibility<Group>(R.id.privateGroup, View.VISIBLE)
            findViewById<TextView>(R.id.lobbyIdWaitShowing).text = model.currentLobbyId
        }
        changeVisibility<Group>(R.id.errorGroup, View.INVISIBLE)

        //check for match making achievements
        AchievementsLibrary.achievementCheck(model.app.getActiveUser()!!,model.lobbyType == "private",AchievementsLibrary.mmtLib)

        ProgressDialogsUtil.dismissProgressDialog()
    }

    override fun onBackPressed() {
        if(isGroupVisible(R.id.setupGroup)) {
            val intent = Intent(applicationContext, GameListActivity::class.java)
            startActivity(intent)
        }else{
            model.leaveMM(false)
        }

    }


}

