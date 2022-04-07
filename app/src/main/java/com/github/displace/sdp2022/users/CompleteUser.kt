package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CompleteUser(private val firebaseUser: FirebaseUser?) {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/", false) as RealTimeDatabase

    private val dbReference: String = if(firebaseUser != null) {
        "CompleteUsers/${firebaseUser.uid}/CompleteUser"
    } else {
        "CompleteUsers/dummy_id/CompleteUser"
    }


    private lateinit var partialUser: PartialUser

    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private var friendsList: MutableList<PartialUser> = mutableListOf()

    private lateinit var gameHistory: MutableList<History>

    init {
        initializeUser()
    }

    private fun addUserToDatabase() {
        db.insert(dbReference, "", this)
    }

    fun addAchievement(ach: Achievement) {
        achievements.add(ach)
        db.update(dbReference, "achievements/${achievements.size - 1}", ach)
    }

    fun updateStats(statName: String, newValue: Long) {
        for (i in 0..stats.size) {
            if (statName == stats[i].name) {
                stats[i].value = newValue
                db.update(dbReference, "stats/$i/value", newValue)
                return
            }
        }
    }

    fun addFriend(partialU: PartialUser) {
        if (!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
            db.update(dbReference, "friendsList/${friendsList.size - 1}", partialU)
        }
    }

    fun removeFriend(partialU: PartialUser) {
        if (containsPartialUser(friendsList, partialU)) {
            friendsList.remove(partialU)
            db.update(dbReference, "friendsList", friendsList)
        }
    }

    /**
     * Check if a partial user is in a list, by using the user id to verify it
     */
    private fun containsPartialUser(pUserList: List<PartialUser>, partialU: PartialUser): Boolean {
        for(f in pUserList) {
            if(f.uid == partialU.uid) {
                return true
            }
        }
        return false
    }

    fun addGameInHistory(map: String, date: String, result: String) {
            val history = History(map, date, result)
            gameHistory.add(history)
            db.update(dbReference, "gameHistory/${gameHistory.size-1}", history)
    }

    fun changeUsername(newName: String) {
        partialUser.username = newName
        db.update(dbReference, "partialUser/username", newName)
    }

    private fun initializeUser() {
        db.referenceGet(dbReference, "").addOnSuccessListener { usr ->
            if(usr.value != null) {
                val completeUser = usr.value as HashMap<String, *>

                // Get achievements from the database
                val achievementsDB = completeUser.get("achievements") as ArrayList<HashMap<String, String>>
                achievements = mutableListOf()
                if(achievementsDB != null) {
                    for(ach in achievementsDB) {
                        achievements.add(Achievement(ach["name"]!!, ach["date"]!!))
                    }
                }

                // Get statistics from the database
                val statsDB = completeUser["stats"] as ArrayList<HashMap<String, String>>
                stats = mutableListOf()
                if(statsDB != null) {
                    for(s in statsDB) {
                        stats.add(Statistic(s["name"]!!, s["value"] as Long))
                    }
                }

                // Get friends list from the database
                val friendsListHash = completeUser["friendsList"] as ArrayList<HashMap<String, String>>
                friendsList = mutableListOf()
                if(friendsListHash != null) {
                    for(f in friendsListHash) {
                        friendsList.add(PartialUser(f["username"]!!, f["uid"]!!))
                    }
                }

                // Get game history from the database
                val gameHistoryHash = completeUser["gameHistory"] as ArrayList<HashMap<String, String>>
                gameHistory = mutableListOf()
                if(gameHistoryHash != null){
                    for(g in gameHistoryHash) {
                        gameHistory.add(History(g["map"]!!, g["date"]!!, g["result"]!!))
                    }
                }

                // Get Partial User from the database
                val partialUserMap = completeUser["partialUser"] as HashMap<String, String>
                partialUser = PartialUser(partialUserMap["username"]!!, partialUserMap.get("uid")!!)
            } else{

                initializeAchievements()
                initializeStats()
                friendsList = mutableListOf(
                    PartialUser("THE SYSTEM", "dummy_id")
                )
                gameHistory = mutableListOf(
                    History("dummy_map", getCurrentDate(), "VICTORY")
                )
                initializePartialUser()
                addUserToDatabase()
                createFirstMessageList()
            }
        }
    }

    private fun createFirstMessageList(){
        val uid = partialUser.uid
        db.update("CompleteUsers/$uid","MessageHistory", listOf(Message("Welcome to DisPlace",getCurrentDate(),PartialUser("THE SYSTEM","dummy_id"))))
    }

    fun removeUserFromDatabase() {
        val uid = partialUser.uid
        db.delete("CompleteUsers",uid)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements(){
        achievements = mutableListOf(
            Achievement("Create your account !", getCurrentDate())
        )
    }

    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }

    private fun initializeStats(){
        stats = mutableListOf(
            Statistic("stat1", 0),
            Statistic("stat2", 0))      // It's a dummy list for now, will be replaced with a list of all the possible statistics initialized to 0
    }

    private fun initializePartialUser() {
        if(firebaseUser != null) {
            if (firebaseUser.displayName == null) {        // maybe add the profile picture later
                partialUser = PartialUser("defaultName", firebaseUser.uid)
            } else {
                partialUser = PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
            }
        } else {
            partialUser = PartialUser("defaultName", "dummy_id")
        }

    }

    fun getPartialUser(): PartialUser {
        return partialUser
    }

    fun getAchievements(): MutableList<Achievement> {
        return achievements
    }

    fun getStats(): List<Statistic> {
        return stats
    }

    fun getFriendsList(): MutableList<PartialUser> {
        return friendsList
    }

    fun getGameHistory(): MutableList<History> {
        return gameHistory
    }

    override fun equals(other: Any?): Boolean {
        val otherUser = other as CompleteUser
        return partialUser == otherUser.getPartialUser()
    }

    override fun hashCode(): Int {
        return partialUser.hashCode()
    }

}