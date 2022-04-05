package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class CompleteUser(private val firebaseUser: FirebaseUser?) : User {

    private val dbReference: String = if (firebaseUser != null) {
        "CompleteUsers/${firebaseUser.uid}/CompleteUser"
    } else {
        "CompleteUsers/dummy_id/CompleteUser"
    }

    private val db: RealTimeDatabase = RealTimeDatabase(true).instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    private lateinit var partialUser: PartialUser

    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private var friendsList: MutableList<PartialUser> = mutableListOf()

    private lateinit var gameHistory: MutableList<History>

    init {
        initializeUser()
        Thread.sleep(3000)
    }

    private fun addUserToDatabase() {
        db.insert(dbReference, "", this)
    }

    override fun addAchievement(ach: Achievement) {
        achievements.add(ach)
        db.update(dbReference, "achievements/${achievements.size - 1}", ach)
    }

    override fun updateStats(statName: String, newValue: Long) {
        for (i in 0..stats.size-1) {
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

    @Suppress("UNCHECKED_CAST")
    private fun initializeUser() {
        db.referenceGet(dbReference, "").addOnSuccessListener { usr ->
            if (usr.value != null) {
                val completeUser = usr.value as HashMap<*, *>

                // Get achievements from the database
                val achievementsDB: ArrayList<HashMap<String, String>>? =
                    completeUser["achievements"] as ArrayList<HashMap<String, String>>
                achievements = mutableListOf()
                if (achievementsDB != null) {
                    for (ach in achievementsDB) {
                        achievements.add(Achievement(ach["name"]!!, ach["date"]!!))
                    }
                }

                // Get statistics from the database
                val statsDB: ArrayList<HashMap<String, String>>? =
                    completeUser["stats"] as ArrayList<HashMap<String, String>>
                stats = mutableListOf()
                if (statsDB != null) {
                    for (s in statsDB) {
                        stats.add(Statistic(s["name"]!!, s["value"] as Long))
                    }
                }

                val friendsListHash: ArrayList<HashMap<String, String>>? =
                    completeUser["friendsList"] as ArrayList<HashMap<String, String>>

                friendsList = mutableListOf()
                if (friendsListHash != null) {
                    for (f in friendsListHash) {
                        friendsList.add(PartialUser(f["username"]!!, f["uid"]!!))
                    }
                }

                val gameHistoryHash: ArrayList<HashMap<String, String>>? =
                    completeUser["gameHistory"] as ArrayList<HashMap<String, String>>
                gameHistory = mutableListOf()
                if (gameHistoryHash != null) {
                    for (g in gameHistoryHash) {
                        gameHistory.add(History(g["map"]!!, g["date"]!!, g["result"]!!))
                    }
                }

                val partialUserMap = completeUser["partialUser"] as HashMap<String, String>
                partialUser = PartialUser(partialUserMap["username"]!!, partialUserMap["uid"]!!)
            } else {
                achievements = initializeAchievements()
                stats = initializeStats()
                friendsList = mutableListOf(
                    PartialUser("dummy_friend_username", "dummy_friend_id")
                )
                gameHistory = mutableListOf(
                    History("dummy_map", getCurrentDate(), "VICTORY")
                )
                initializePartialUser()
                addUserToDatabase()
            }
        }
    }

    override fun removeUserFromDatabase() {
        db.delete(dbReference, "")
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements(): MutableList<Achievement> {
        return mutableListOf(
            Achievement("Create your account !", getCurrentDate())
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }

    private fun initializeStats(): MutableList<Statistic> {
        return mutableListOf(
            Statistic("stat1", 0),
            Statistic("stat2", 0)
        )      // It's a dummy list for now, will be replaced with a list of all the possible statistics initialized to 0
    }

    private fun initializePartialUser() {
        partialUser = if (firebaseUser != null) {
            if (firebaseUser.displayName == null) {        // maybe add the profile picture later
                PartialUser("defaultName", firebaseUser.uid)
            } else {
                PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
            }
        } else {
            PartialUser("defaultName", "dummy_id")
        }

    }

    override fun getPartialUser(): PartialUser? {
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