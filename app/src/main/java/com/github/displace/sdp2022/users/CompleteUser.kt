package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextUInt

class CompleteUser(
    context: Context?,
    private val firebaseUser: FirebaseUser?,
    val guestBoolean: Boolean = false,
    val offlineMode: Boolean = false,
) {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate(
        "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",
        false
    ) as RealTimeDatabase

    private val guestNumber = Random.nextUInt()

    private val dbReference: String = if (firebaseUser != null) {
        "CompleteUsers/${firebaseUser.uid}/CompleteUser"
    } else {
        if (guestBoolean) {
            "CompleteUsers/guest_$guestNumber/CompleteUser"
        } else if(offlineMode) {
            ""  // useless
        } else {
            "CompleteUsers/dummy_id/CompleteUser"
        }
    }

    private val offlineUserFetcher: OfflineUserFetcher = OfflineUserFetcher(context)

    private var partialUser: PartialUser = PartialUser("this cannot be a user", "still cannot be a user")

    private lateinit var googleName: String

    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private lateinit var friendsList: MutableList<PartialUser>
    private lateinit var gameHistory: MutableList<History>
    private var profilePic: Bitmap? = null

    init {
        initializeUser()
    }

    private fun addUserToDatabase() {
        db.insert(dbReference, "", this)
    }


    fun setCompleteUser(
        partialUser: PartialUser,
        achievements: MutableList<Achievement>,
        stats: MutableList<Statistic>,
        friendsList: MutableList<PartialUser>,
        gameHistory: MutableList<History>
    ) {
        this.partialUser = partialUser
        this.achievements = achievements
        this.stats = stats
        this.friendsList = friendsList
        this.gameHistory = gameHistory
    }


    fun addAchievement(ach: Achievement) {
        if (offlineMode)
            return

        achievements.add(ach)
        db.update(dbReference, "achievements/${achievements.size - 1}", ach)
        if(!guestBoolean) {
            offlineUserFetcher.setOfflineAchievements(achievements)
        }
    }

    fun updateStats(statName: String, newValue: Long) {
        if (offlineMode)
            return
        for (i in 0..stats.size) {
            if (statName == stats[i].name) {
                stats[i].value = newValue
                db.update(dbReference, "stats/$i/value", newValue)

                if(!guestBoolean) {
                    offlineUserFetcher.setOfflineStats(stats)
                }
                return
            }
        }
    }

    fun addFriend(partialU: PartialUser) {
        if (offlineMode)
            return

        if (!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
            db.update(dbReference, "friendsList/${friendsList.size - 1}", partialU)

            if(!guestBoolean) {
                offlineUserFetcher.setOfflineFriendsList(friendsList)
            }

        }
    }

    fun removeFriend(partialU: PartialUser) {
        if (offlineMode)
            return

        if (friendsList.remove(partialU)) {
            if(!guestBoolean) {
                offlineUserFetcher.setOfflineFriendsList(friendsList)
            }
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

    fun addGameInHistory(map: String, date: String, result: String) {
        if (offlineMode)
            return

        val history = History(map, date, result)
        gameHistory.add(history)
        if(!guestBoolean) {
            offlineUserFetcher.setOfflineGameHistory(gameHistory)
        }
        db.update(dbReference, "gameHistory/${gameHistory.size - 1}", history)
    }

    fun changeUsername(newName: String) {
        if (offlineMode)
            return

        partialUser.username = newName
        offlineUserFetcher.setOfflinePartialUser(partialUser)
        db.update(dbReference, "username", newName)
    }

    fun setProfilePic(pic: Bitmap) {
        profilePic = pic
    }

    fun getProfilePic(): Bitmap? {
        return profilePic
    }

    private fun initializeUser() {
        // Initialization if it's a guest
        if (guestBoolean) {
            initializePartialUser()

            initializeAchievements()
            initializeStats()
            friendsList = mutableListOf(
                PartialUser("THE SYSTEM", "dummy_friend_id")
            )
            gameHistory = mutableListOf(
                History("dummy_map", getCurrentDate(), "VICTORY")
            )
            createFirstMessageList()
            return
        }


        // Initialization if the user is offline, using the cache
        if (offlineMode) {
            achievements = offlineUserFetcher.getOfflineAchievements()
            stats = offlineUserFetcher.getOfflineStats()
            friendsList = offlineUserFetcher.getOfflineFriendsList()
            gameHistory = offlineUserFetcher.getOfflineGameHistory()
            partialUser = offlineUserFetcher.getOfflinePartialUser()
            return
        }


        // Initialization if the user is online
        db.referenceGet(dbReference, "").addOnSuccessListener { usr ->
            if (usr.value != null) {
                val completeUser = usr.value as HashMap<String, *>

                // Get achievements from the database
                val achievementsDB =
                    completeUser["achievements"] as ArrayList<HashMap<String, String>>

                achievements = achievementsDB.map { ach ->
                    Achievement(
                        ach["name"]!!,
                        ach["date"]!!
                    )
                } as MutableList<Achievement>

                // Get statistics from the database
                val statsDB = completeUser["stats"] as ArrayList<HashMap<String, String>>
                stats = statsDB.map { s ->
                    Statistic(
                        s["name"]!!,
                        s["value"] as Long
                    )
                } as MutableList<Statistic>

                // Get friends list from the database
                val friendsListHash =
                    completeUser["friendsList"] as ArrayList<HashMap<String, String>>

                friendsList = friendsListHash.map { f ->
                    PartialUser(
                        f["username"]!!,
                        f["uid"]!!
                    )
                } as MutableList<PartialUser>

                // Get game history from the database
                val gameHistoryHash =
                    completeUser["gameHistory"] as ArrayList<HashMap<String, String>>
                gameHistory = gameHistoryHash.map { g ->
                    History(
                        g["map"]!!,
                        g["date"]!!,
                        g["result"]!!
                    )
                } as MutableList<History>

                // Get Partial User from the database
                val partialUserMap = completeUser["partialUser"] as HashMap<String, String>
                partialUser =
                    PartialUser(partialUserMap["username"]!!, partialUserMap["uid"]!!)

                offlineUserFetcher.setCompleteUser(this)
            } else {    // if user not existing in database, initialize it and adding it to database

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

    fun removeUserFromDatabase() {
        db.delete("CompleteUsers", partialUser.uid)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements() {
        achievements = mutableListOf(
            Achievement("Create your account !", getCurrentDate())
        )

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflineAchievements(achievements)
        }

        db.update(
            "CompleteUsers/${partialUser.uid}",
            "achievements",
            achievements
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }

    private fun initializeStats() {
        stats = mutableListOf(
            Statistic("nbWin", 0),
            Statistic("totalTime", 0),
            Statistic("totalDist", 0)
        )

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflineStats(stats)
        }

        db.update(
            "CompleteUsers/${partialUser.uid}",
            "Stats",
            stats
        )
    }

    private fun initializePartialUser() {
        googleName = "defaultName"
        if (firebaseUser != null) {
            if (firebaseUser.displayName == null) {
                setupDefaultOrGuestPartialUser()
            } else {
                partialUser = PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
                googleName = firebaseUser.displayName!!
            }
        } else {
            setupDefaultOrGuestPartialUser()
        }

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflinePartialUser(partialUser)
        }

        db.update(
            "CompleteUsers/${partialUser.uid}",
            "PartialUser",
            partialUser
        )

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

    private fun setupDefaultOrGuestPartialUser() {
        partialUser = if (guestBoolean) {
            PartialUser("Guest$guestNumber", "guest_$guestNumber")
        } else {
            PartialUser("defaultName", "dummy_id")
        }
    }

    fun cacheMessages(msgList: ArrayList<Message>) {
        offlineUserFetcher.setOfflineMessageHistory(msgList)
    }

    fun getMessageHistory(): ArrayList<Message> {
        return offlineUserFetcher.getOfflineMessageHistory()
    }

}