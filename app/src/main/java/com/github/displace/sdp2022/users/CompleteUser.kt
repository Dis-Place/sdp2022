package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.database.CleanUpGuests
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Class that contains all the infos of an user
 * @param context: Application context, used to get the cached infos
 * @param db: Database
 * @param guestBoolean: If the user is a guest
 * @param offlineMode: If the user is in offline mode
 * @param remembered: If the application remembers the user and has its information cached
 * @param activity: Activity from where the User is initialized, used to dismiss the progress dialog that waits for the user to be initialize
 */
class CompleteUser(
    context: Context?,
    private val firebaseUser: FirebaseUser?,
    private val db: GoodDB,
    val guestBoolean: Boolean = false,
    var offlineMode: Boolean = false,
    val remembered: Boolean = false,
    val activity: SignInActivity? = null
) {

    // Reference of the CompleteUser in the database
    private val dbReference: String = if (firebaseUser != null) {
        if(guestBoolean) {
            "CompleteUsers/guest_${firebaseUser.uid}/CompleteUser"  // We add "guest-" to retain this information in the database
        } else {
            "CompleteUsers/${firebaseUser.uid}/CompleteUser"    // Basic reference
        }
    } else {
        if(offlineMode) {
            ""  // useless, since in offline mode we never use the database
        } else {
            "CompleteUsers/dummy_id/CompleteUser"       // This case if for testing
        }
    }

    private val app : MyApplication = context as MyApplication  // application context

    private val guestNumber = Random.nextUInt()     // random number to differentiate guest users in a game
    private var guestIndex: Int = -1        // Index for the guests in DB, useful for cleaning up the database from the unused guests

    private val offlineUserFetcher: OfflineUserFetcher = OfflineUserFetcher(context)    // The offlineUserFetcher contains all the methods to get cached information

    // Contains very basic information about the user
    private var partialUser: PartialUser = PartialUser("this cannot be a user", "still cannot be a user")

    // Profile informations
    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private lateinit var friendsList: MutableList<PartialUser>
    private var gameHistory: MutableList<History> = mutableListOf()

    private var profilePic: Bitmap? = null      // Stores the profile picture bitmap to prevent going to the database every time


    init {
        initializeUser()
    }

    /**
     * Add the user to the database
     * Adding the achievements, statistics, friend's list, game history and partial user
     */
    private fun addUserToDatabase() {
        db.update("$dbReference/achievements", achievements.map { a -> a.toMap() })
        db.update("$dbReference/stats", stats.map { s -> s.toMap() })
        db.update("$dbReference/friendsList", friendsList.map { f -> f.toMap() })
        db.update("$dbReference/gameHistory", gameHistory.map { h -> h.toMap() })
        db.update("$dbReference/partialUser", partialUser.toMap())

        // If the user is a guest, we have to increment all the guest indexes of all guests in the database,
        // and remove those that are too old
        if(guestBoolean) {
            if(firebaseUser != null) {
                CleanUpGuests.updateGuestIndexesAndCleanUpDatabase(db, "guest_${firebaseUser.uid}")
                db.update("$dbReference/guestIndex", guestIndex)
            }
        }
    }

    fun addAchievement(ach: Achievement) {
        if (offlineMode)
            return

        if(!achievements.contains(ach)){

            /**
             * This part should also send a notification
             */
            app.getMessageHandler().messageNotification(ach.description,ach.name)

            achievements.add(ach)
            db.update("$dbReference/achievements", achievements.map { a -> a.toMap() })  // We modify the entire list of stats to fit with the MockDB, and it's better practice
            if(!guestBoolean) {
                offlineUserFetcher.setOfflineAchievements(achievements)
            }
        }

    }

    fun updateStats(statName: String, newValue: Long) {
        if (offlineMode)
            return
        for (i in 0 until stats.size) {
            if (statName == stats[i].name) {
                stats[i].value = newValue
                db.update("$dbReference/stats", stats.map { s -> s.toMap() })      // We modify the entire list of stats to fit with the MockDB, and it's better practice

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
            db.update("$dbReference/friendsList", friendsList.map { f -> f.toMap() })    // We modify the entire list of stats to fit with the MockDB, and it's better practice

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
            db.update("$dbReference/friendsList", friendsList.map { f -> f.toMap() })
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
        db.update("$dbReference/gameHistory", gameHistory.map { h -> h.toMap() })  // We modify the entire list of stats to fit with the MockDB, and it's better practice
    }

    fun changeUsername(newName: String) {
        if (offlineMode)
            return

        partialUser.username = newName
        offlineUserFetcher.setOfflinePartialUser(partialUser)
        db.update("$dbReference/partialUser/username", newName)
    }

    fun setProfilePic(pic: Bitmap) {
        profilePic = pic
    }

    fun getProfilePic(): Bitmap? {
        return profilePic
    }

    fun setOffline(offline: Boolean) {
        offlineMode = offline
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
            gameHistory = mutableListOf()
            createFirstMessageList()

            guestIndex = 0

            addUserToDatabase()

            activity?.launchMainMenuActivity()
            return
        }


        // Initialization if the user is offline, using the cache
        if (offlineMode || remembered) {
            achievements = offlineUserFetcher.getOfflineAchievements()
            stats = offlineUserFetcher.getOfflineStats()
            friendsList = offlineUserFetcher.getOfflineFriendsList()
            gameHistory = offlineUserFetcher.getOfflineGameHistory()
            partialUser = offlineUserFetcher.getOfflinePartialUser()
            activity?.launchMainMenuActivity()
            return
        }


        // Initialization if the user is online

        db.getThenCall<HashMap<String, *>>(dbReference){completeUser ->
            if(completeUser != null) {
                // Get statistics from the database
                val statsDB = completeUser["stats"] as List<HashMap<String, String>>
                stats = statsDB.map { s ->
                    Statistic(
                        s["name"]!!,
                        s["value"] as Long
                    )
                } as MutableList<Statistic>


                // Get achievements from the database
                val achievementsDB =
                    completeUser["achievements"] as List<HashMap<String, String>>

                achievements = achievementsDB.map { ach ->
                    Achievement(
                        ach["name"]!!,
                        ach["description"]!!,
                        ach["date"]!!
                    )
                } as MutableList<Achievement>

                // Get friends list from the database
                val friendsListHash =
                    completeUser["friendsList"] as List<HashMap<String, String>>

                friendsList = friendsListHash.map { f ->
                    PartialUser(
                        f["username"]!!,
                        f["uid"]!!
                    )
                } as MutableList<PartialUser>

                // Get game history from the database
                val gameHistoryHash =
                    completeUser["gameHistory"] as List<HashMap<String, String>>?
                if(gameHistoryHash != null) {
                    gameHistory = gameHistoryHash.map { g ->
                        History(
                            g["map"]!!,
                            g["date"]!!,
                            g["result"]!!
                        )
                    } as MutableList<History>
                }
                // Get Partial User from the database
                val partialUserMap = completeUser["partialUser"] as Map<String, String>
                partialUser =
                    PartialUser(partialUserMap["username"]!!, partialUserMap["uid"]!!)

                offlineUserFetcher.setCompleteUser(this)
            } else {    // if user not existing in database, initialize it and adding it to database

                initializePartialUser()
                initializeAchievements()
                initializeStats()
                friendsList = mutableListOf(
                    PartialUser("THE SYSTEM", "dummy_friend_id")
                )
                gameHistory = mutableListOf()
                addUserToDatabase()
                createFirstMessageList()
            }
            activity?.launchMainMenuActivity()
        }

    }

    private fun createFirstMessageList() {
        db.update("$dbReference/MessageHistory",
            listOf(
                Message(
                    "Welcome to DisPlace",
                    getCurrentDate(),
                    PartialUser("THE SYSTEM", "dummy_id")
                ).toMap()
            )
        )
    }

    fun removeUserFromDatabase() {
        db.delete("CompleteUsers/${partialUser.uid}")
        firebaseUser?.delete()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements() {
        achievements = mutableListOf(
            Achievement("Welcome home!","Create your account", getCurrentDate())
        )

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflineAchievements(achievements)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }

    private fun initializeStats() {
        stats = mutableListOf(
            Statistic("Games Played", 0),
            Statistic("Games Won", 0),
            Statistic("Distance Moved", 0)
        )      // It's a dummy list for now, will be replaced with a list of all the possible statistics initialized to 0

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflineStats(stats)
        }
    }

    private fun initializePartialUser() {
        if (firebaseUser != null) {
            if (firebaseUser.displayName == null || firebaseUser.displayName == "") {
                setupDefaultOrGuestPartialUser()
            } else {
                partialUser = PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
            }
        } else {
            setupDefaultOrGuestPartialUser()
        }

        if(!guestBoolean && firebaseUser != null) {
            offlineUserFetcher.setOfflinePartialUser(partialUser)
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

    fun getStat(name : String): Statistic {
        for(stat in stats){
            if(stat.name == name){
                return stat
            }
        }
        return Statistic("ERROR",0)
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
            PartialUser("Guest$guestNumber", "guest_${firebaseUser?.uid}")
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