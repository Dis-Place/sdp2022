package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.github.displace.sdp2022.ImageDatabase
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random
import kotlin.random.nextUInt

class CompleteUser(private val firebaseUser: FirebaseUser?, val guestBoolean: Boolean) : User {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    private val guestNumber = Random.nextUInt()

    private val dbReference: String = if (firebaseUser != null) {
        "CompleteUsers/${firebaseUser.uid}/CompleteUser"
    } else {
        if(guestBoolean) {
            "CompleteUsers/guest_$guestNumber/CompleteUser"
        } else {
            "CompleteUsers/dummy_id/CompleteUser"
        }
    }


    private lateinit var partialUser: PartialUser

    private lateinit var googleName: String

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

    override fun addAchievement(ach: Achievement) {
        achievements.add(ach)
        db.update(dbReference, "achievements/${achievements.size - 1}", ach)
    }

    override fun updateStats(statName: String, newValue: Long) {
        for (i in 0..stats.size) {
            if (statName == stats[i].name) {
                stats[i].value = newValue
                db.update(dbReference, "stats/$i/value", newValue)
                return
            }
        }
    }

    override fun addFriend(partialU: PartialUser) {
        if (!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
            db.update(dbReference, "friendsList/${friendsList.size - 1}", partialU)
        }
    }

    override fun removeFriend(partialU: PartialUser) {
        if (containsPartialUser(friendsList, partialU)) {
            friendsList.remove(partialU)
            db.update(dbReference, "friendsList", friendsList)
        }
    }

    /**
     * Check if a partial user is in a list, by using the user id to verify it
     */
    private fun containsPartialUser(pUserList: List<PartialUser>, partialU: PartialUser): Boolean {
        for (f in pUserList) {
            if (f.uid == partialU.uid) {
                return true
            }
        }
        return false
    }

    override fun addGameInHistory(map: String, date: String, result: String) {
        val history = History(map, date, result)
        gameHistory.add(history)
        db.update(dbReference, "gameHistory/${gameHistory.size - 1}", history)
    }

    override fun changeUsername(newName: String) {
        partialUser.username = newName
        db.update(dbReference, "partialUser/username", newName)
    }

    private fun initializeUser() {
        if(guestBoolean) {
            initializeAchievements()
            initializeStats()
            friendsList = mutableListOf(
                PartialUser("THE SYSTEM", "dummy_friend_id")
            )
            gameHistory = mutableListOf(
                History("dummy_map", getCurrentDate(), "VICTORY")
            )
            initializePartialUser()
            addUserToDatabase()
            createFirstMessageList()
        } else {
            db.referenceGet(dbReference, "").addOnSuccessListener { usr ->
                if (usr.value != null) {
                    val completeUser = usr.value as HashMap<String, *>

                    // Get achievements from the database
                    val achievementsDB =
                        completeUser.get("achievements") as ArrayList<HashMap<String, String>>

                    achievements = achievementsDB.map{ ach -> Achievement(ach["name"]!!, ach["date"]!!) } as MutableList<Achievement>

                    // Get statistics from the database
                    val statsDB = completeUser["stats"] as ArrayList<HashMap<String, String>>
                    stats = statsDB.map { s -> Statistic(s["name"]!!, s["value"] as Long)} as MutableList<Statistic>

                    // Get friends list from the database
                    val friendsListHash =
                        completeUser["friendsList"] as ArrayList<HashMap<String, String>>

                    friendsList = friendsListHash.map { f -> PartialUser(f["username"]!!, f["uid"]!!) } as MutableList<PartialUser>

                    // Get game history from the database
                    val gameHistoryHash =
                        completeUser["gameHistory"] as ArrayList<HashMap<String, String>>
                    gameHistory = gameHistoryHash.map { g -> History(g["map"]!!, g["date"]!!, g["result"]!!)} as MutableList<History>

                    // Get Partial User from the database
                    val partialUserMap = completeUser["partialUser"] as HashMap<String, String>
                    partialUser =
                        PartialUser(partialUserMap["username"]!!, partialUserMap.get("uid")!!)
                } else {

                    initializeAchievements()
                    initializeStats()
                    friendsList = mutableListOf(
                        PartialUser("THE SYSTEM", "dummy_friend_id")
                    )
                    gameHistory = mutableListOf(
                        History("dummy_map", getCurrentDate(), "VICTORY")
                    )
                    initializePartialUser()
                    addUserToDatabase()
                    createFirstMessageList()
                }
            }.addOnFailureListener { e ->
                e.message?.let { Log.e("DBFailure", it) }
            }
        }
    }

    private fun createFirstMessageList() {
        val uid = partialUser.uid
        db.update(
            "CompleteUsers/$uid",
            "MessageHistory",
            listOf(
                Message(
                    "Welcome to DisPlace",
                    getCurrentDate(),
                    PartialUser("THE SYSTEM", "dummy_id")
                )
            )
        )
    }

    override fun removeUserFromDatabase() {
        val uid = partialUser.uid
        db.delete("CompleteUsers", uid)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements(){
        achievements = mutableListOf(
            Achievement("Create your account !", getCurrentDate())
        )
    }

    @SuppressLint("SimpleDateFormat")
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
        googleName = "defaultName"
        if (firebaseUser != null) {
            if (firebaseUser.displayName == null) {        // maybe add the profile picture later
                partialUser = PartialUser("defaultName", firebaseUser.uid)
            } else {
                partialUser = PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
                googleName = firebaseUser.displayName!!
            }
        } else {
            if(guestBoolean) {
                partialUser = PartialUser("Guest$guestNumber", "guest_$guestNumber")
            } else {
                partialUser = PartialUser("defaultName", "dummy_id")
            }
        }

    }

    override fun getPartialUser(): PartialUser {
        return partialUser
    }

    override fun getAchievements(): MutableList<Achievement> {
        return achievements
    }

    override fun getStats(): List<Statistic> {
        return stats
    }

    override fun getFriendsList(): MutableList<PartialUser> {
        return friendsList
    }

    override fun getGameHistory(): MutableList<History> {
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