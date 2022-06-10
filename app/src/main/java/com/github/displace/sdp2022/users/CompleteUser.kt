package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.authentication.AuthenticatedUser
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.database.CleanUpGuests
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.util.DateTimeUtil
import com.google.firebase.auth.FirebaseUser
import kotlin.collections.ArrayList
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
    private val authenticatedUser: AuthenticatedUser?,
    private val db: GoodDB,
    val guestBoolean: Boolean = false,
    var offlineMode: Boolean = false,
    val remembered: Boolean = false,
    val activity: SignInActivity? = null
) {
    // Reference of the CompleteUser in the database
    private var dbReference: String = if (authenticatedUser != null) {
        if(guestBoolean) {
            "CompleteUsers/guest_${authenticatedUser.uid()}/CompleteUser"  // We add "guest-" to retain the fact it is a guest in the database
        } else {
            "CompleteUsers/${authenticatedUser.uid()}/CompleteUser"    // Basic reference
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

    private val offlineUserFetcher: OfflineUserFetcher = OfflineUserFetcher(context)    // The offlineUserFetcher contains all the methods to get cached information

    // Contains very basic information about the user
    private lateinit var partialUser: PartialUser // = PartialUser("this cannot be a user", "still cannot be a user")

    // Profile informations
    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private lateinit var friendsList: MutableList<PartialUser>
    private var gameHistory: MutableList<History> = mutableListOf()

    private var profilePic: Bitmap? = null      // Stores the profile picture bitmap to prevent going to the database every time

    init {
        if (guestBoolean) {
            initializeNewUser()
        } else if (offlineMode || remembered) { // Initialization if the user is offline, or if the user is cached
            initializeUserWithLocalMemory()
        } else {    // Initialization if the user is online and not cached, we have to search the infos in the database
            initializeUserFromDB()
            Log.d("dbReference", "before $dbReference")
            val regex = """"CompleteUsers/guest_*/CompleteUser"""".toRegex()
            if( regex.containsMatchIn(dbReference) && partialUser != null){
                dbReference = "CompleteUsers/${partialUser.uid}/CompleteUser"
            }
            Log.d("dbReference", "after $dbReference")
        }
    }

    /**
     * Initialization helper methods
     */

    /**
     * Initializes a user that isn't in the database
     * Either a new user or a guest user
     */
    private fun initializeNewUser() {
        // Basic initialization of a user
        initializePartialUser()

        initializeAchievements()
        initializeStats()
        friendsList = mutableListOf(
            PartialUser("THE SYSTEM", "aB34b77tSrdPwJ0pPCvoAdrMliC3")
        )
        gameHistory = mutableListOf()
        createFirstMessageList()

        addUserToDatabase()

        activity?.launchMainMenuActivity()  // If the user have been created in the login, we have to launch the main menu now that the user is initialized
    }

    /**
     * Initializes the user by using the Offline User Fetcher to get the informations from local memory
     */
    private fun initializeUserWithLocalMemory() {
        achievements = offlineUserFetcher.getOfflineAchievements()
        stats = offlineUserFetcher.getOfflineStats()
        friendsList = offlineUserFetcher.getOfflineFriendsList()
        gameHistory = offlineUserFetcher.getOfflineGameHistory()
        partialUser = offlineUserFetcher.getOfflinePartialUser()
        activity?.launchMainMenuActivity()  // If the user have been created in the login, we have to launch the main menu now that the user is initialized
    }

    /**
     * Initializes the user by reading the database to get the informations
     */
    private fun initializeUserFromDB() {
        db.getThenCall<Map<String, *>>(dbReference){completeUser ->
            if(completeUser != null) {
                initializeUserFromDBInfo(completeUser)
            } else {    // if the user doesn't exist in database, it's a new user
                initializeNewUser()
            }
        }
    }

    /**
     * Initializes the user given the database informations
     * @param completeUserInfos: User informations from the database
     */
    private fun initializeUserFromDBInfo(completeUserInfos: Map<String, *>) {
        // Get statistics from the database
        val statsDB = completeUserInfos["stats"] as List<Map<String, String>>
        stats = statsDB.map { s ->
            Statistic(
                s["name"]!!,
                s["value"] as Long
            )
        } as MutableList<Statistic>


        // Get achievements from the database
        val achievementsDB =
            completeUserInfos["achievements"] as List<Map<String, String>>

        achievements = achievementsDB.map { ach ->
            Achievement(
                ach["name"]!!,
                ach["description"]!!,
                ach["date"]!!
            )
        } as MutableList<Achievement>

        // Get friends list from the database
        val friendsListHash =
            completeUserInfos["friendsList"] as List<Map<String, String>>

        friendsList = friendsListHash.map { f ->
            PartialUser(
                f["username"]!!,
                f["uid"]!!
            )
        } as MutableList<PartialUser>

        // Get game history from the database
        val gameHistoryHash =
            completeUserInfos["gameHistory"] as List<Map<String, String>>?
        if(gameHistoryHash != null) {
            gameHistory = gameHistoryHash.map { g ->
                History(
                    g["gameMode"]!!,
                    g["date"]!!,
                    g["result"]!!
                )
            } as MutableList<History>
        }

        // Get Partial User from the database
        val partialUserMap = completeUserInfos["partialUser"] as Map<String, String>
        partialUser =
            PartialUser(partialUserMap["username"]!!, partialUserMap["uid"]!!)

        // Save the user in the cache
        offlineUserFetcher.setCompleteUser(this)

        activity?.launchMainMenuActivity()  // If the user have been created in the login, we have to launch the main menu now that the user is initialized
    }

    /**
     * Creates the first achievements' list of a new user
     */
    @SuppressLint("SimpleDateFormat")
    private fun initializeAchievements() {
        achievements = mutableListOf(
            Achievement("Welcome home!","Create your account", DateTimeUtil.currentDate())
        )

        if(!guestBoolean && authenticatedUser != null) {     // if firebaseUser is null, it's either an error or a automatic test
            offlineUserFetcher.setOfflineAchievements(achievements)
        }
    }

    /**
     * Creates the first stats' list of a new user
     */
    private fun initializeStats() {
        stats = mutableListOf(
            Statistic("Games Played", 0),
            Statistic("Games Won", 0),
            Statistic("Distance Moved", 0)
        )

        if(!guestBoolean && authenticatedUser != null) {     // if firebaseUser is null, it's either an error or a automatic test
            offlineUserFetcher.setOfflineStats(stats)
        }
    }

    /**
     * Creates the partial user of a new user, based on the fact that it is a guest or not, and on its firebase authentication
     */
    private fun initializePartialUser() {
        if (authenticatedUser != null) {
            if (authenticatedUser.displayName() == null || authenticatedUser.displayName() == "") {   // A firebaseUser without a name is an anonymous user (a guest)
                partialUser = PartialUser("Guest$guestNumber", "guest_${authenticatedUser.uid()}")
            } else {        // We use the display name of the google user as the default name, and the firebaseUser id as an id
                partialUser = PartialUser(authenticatedUser.displayName()!!, authenticatedUser.uid())
            }
        } else {    // For testing
            partialUser = PartialUser("defaultName", "dummy_id")
        }

        if(!guestBoolean && authenticatedUser != null) {     // if firebaseUser is null, it's either an error or a automatic test
            offlineUserFetcher.setOfflinePartialUser(partialUser)
        }
    }

    /**
     * Creates the first message of a new user (which is a single message with "the system")
     */
    private fun createFirstMessageList() {
        val msgs = arrayListOf(
            Message(
                "Welcome to DisPlace",
                DateTimeUtil.currentDate(),
                PartialUser("THE SYSTEM", "aB34b77tSrdPwJ0pPCvoAdrMliC3")
            )
        )

        cacheMessages(msgs)

        db.update("$dbReference/MessageHistory",
            msgs.map { msg -> msg.toMap() }.toList()
        )


    }

    /**
     * Methods for the database
     */

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
            if(authenticatedUser != null) {
                CleanUpGuests.updateGuestIndexesAndCleanUpDatabase(db, "guest_${authenticatedUser.uid()}")
                db.update("$dbReference/guestIndex", 0L)    // Index for the guests in DB, useful for cleaning up the database from the unused guests
            }
        }
    }

    /**
     * Deletes the user from the database
     */
    fun removeUserFromDatabase() {
        db.delete("CompleteUsers/${partialUser.uid}")
        authenticatedUser?.delete()
    }

    /**
     * Methods to update the profile infos
     */

    /**
     * Adds an achievement to the achievements' list
     * @param ach: New achievement
     */
    fun addAchievement(ach: Achievement) {
        if (offlineMode)        // Can't win a achievement when offline
            return

        if(!achievements.map{ i -> i.name}.contains(ach.name)){

            achievements.add(ach)

            /**
             * This part also sends a notification
             */
            app.getMessageHandler().messageNotification(ach.description,ach.name)

            db.update("$dbReference/achievements", achievements.map { a -> a.toMap() })  // We modify the entire list of stats because it's better practice when using the database
            if(!guestBoolean) {     // if the user is a guest, we do not cache the achievement since at the next use of the application it will be erased
                offlineUserFetcher.setOfflineAchievements(achievements)
            }
        }

    }

    /**
     * Updates a specific statistic to a new value
     * @param statName: Name of the statistic to update
     * @param newValue: New value for the statistic
     */
    fun updateStats(statName: String, newValue: Long) {
        if (offlineMode)        // Can't update your stats when offline
            return

        for (i in 0 until stats.size) {         // Search for the corresponding stat
            if (statName == stats[i].name) {
                stats[i].value = newValue
                db.update("$dbReference/stats", stats.map { s -> s.toMap() })      // We modify the entire list of stats because it's better practice
                                                                                            // when using the database

                if(!guestBoolean) { // if the user is a guest, we do not cache the stats since at the next use of the application it will be erased
                    offlineUserFetcher.setOfflineStats(stats)
                }
                return
            }
        }
    }

    /**
     * Adds a friend to the friend's list
     * @param partialU: Basic infos of the new friend
     */
    fun addFriend(partialU: PartialUser, toDb : Boolean) {
        if (offlineMode)        // Can't add a friend when offline
            return

    //    if (!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
            Log.d("Cuser", " friends $friendsList")
            if( toDb){
                Log.d("Cuser", "adding friend $partialU")
                Log.d("Cuser", "reference $dbReference/friendsList")
                db.update("$dbReference/friendsList", friendsList.map { f -> f.toMap() })    // We modify the entire list of stats because it's better practice

            }
                                                                                                // when using the database

            if(!guestBoolean) { // if the user is a guest, we do not cache the friends' list since at the next use of the application it will be erased
                offlineUserFetcher.setOfflineFriendsList(friendsList)
            }

    //    }
    }

    /**
     * Removes a friend from the friend's list
     * @param partialU: Basic infos of the friend we want to remove
     */
    fun removeFriend(partialU: PartialUser) {
        if (offlineMode)        // Can't remove a friend when offline
            return

        if (friendsList.remove(partialU)) {
            if(!guestBoolean) { // if the user is a guest, we do not cache the friends' list since at the next use of the application it will be erased
                offlineUserFetcher.setOfflineFriendsList(friendsList)
            }
            db.update("$dbReference/friendsList", friendsList.map { f -> f.toMap() })
        }
    }

    /**
     * Updates the the users friend list, called by listener when there is a change in the database
     * @param newList: list of friends in the database
     */
    fun updateFriendList( newList : MutableList<PartialUser>){
        friendsList = if (newList.size == 0){
            mutableListOf(
                PartialUser("THE SYSTEM", "dummy_friend_id"))
        } else {
            newList
        }
    }

    /**
     * Add the info of a new game in the game history
     * @param map: Map of the game
     * @param date: Date of the game
     * @param result: Result of the game
     */
    fun addGameInHistory(map: String, date: String, result: String) {
        if (offlineMode)    // Can't add a new game when offline
            return

        val history = History(map, date, result)
        gameHistory.add(history)
        if(!guestBoolean) { // if the user is a guest, we do not cache the game history since at the next use of the application it will be erased
            offlineUserFetcher.setOfflineGameHistory(gameHistory)
        }
        db.update("$dbReference/gameHistory", gameHistory.map { h -> h.toMap() })  // We modify the entire list of stats to fit with the MockDB, and it's better practice
    }

    /**
     * Updates the username
     */
    fun changeUsername(newName: String) {
        if (offlineMode || guestBoolean)    // Can't change username when offline or when a guest
            return

        partialUser.username = newName
        offlineUserFetcher.setOfflinePartialUser(partialUser)
        db.update("$dbReference/partialUser/username", newName)
    }

    /**
     * Setter for the profile picture
     */
    fun setProfilePic(pic: Bitmap) {
        profilePic = pic
    }

    /**
     * Changes the status of the user
     */
    fun setOffline(offline: Boolean) {
        offlineMode = offline
    }

    /**
     * Helper methods
     */

    /**
     * Checks if a partial user is in a list, by using the user id to verify it
     * @param pUserList: List of partial users
     * @param partialU: Partial User we're searching
     */
    private fun containsPartialUser(pUserList: List<PartialUser>, partialU: PartialUser): Boolean {
        for (f in pUserList) {
            if (f.uid == partialU.uid) {
                return true
            }
        }
        return false
    }

    /**
     * Getter methods
     */

    /**
     * Getter for the Partial User
     */
    fun getPartialUser(): PartialUser {
        return partialUser
    }

    /**
     * Getter for the achievements' list
     */
    fun getAchievements(): MutableList<Achievement> {
        return achievements
    }

    /**
     * Getter for the stats' list
     */
    fun getStats(): List<Statistic> {
        return stats
    }

    /**
     * Getter for the value of a specific statistic
     */
    fun getStat(name : String): Statistic {
        for(stat in stats){
            if(stat.name == name){
                return stat
            }
        }
        return Statistic("ERROR",0)
    }

    /**
     * Getter for the friends' list
     */
    fun getFriendsList(): List<PartialUser> {
        return friendsList
    }

    /**
     * Getter for the game history
     */
    fun getGameHistory(): List<History> {
        return gameHistory
    }

    /**
     * Getter for the message history
     */
    fun getMessageHistory(): List<Message> {
        return offlineUserFetcher.getOfflineMessageHistory().toList()
    }

    /**
     * Getter for the profile picture
     */
    fun getProfilePic(): Bitmap? {
        return profilePic
    }

    /**
     * Other
     */

    /**
     * Caches the message list
     */
    fun cacheMessages(msgList: ArrayList<Message>) {
        offlineUserFetcher.setOfflineMessageHistory(msgList)
    }

    override fun equals(other: Any?): Boolean {
        val otherUser = other as CompleteUser
        return partialUser == otherUser.getPartialUser()
    }

    override fun hashCode(): Int {
        return partialUser.hashCode()
    }


}