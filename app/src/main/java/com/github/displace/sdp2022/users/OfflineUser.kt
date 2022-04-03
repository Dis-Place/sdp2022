package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val PARTIAL_USER_PATH = "user/partial"
private const val ACHIEVEMENT_PATH = "user/partial"
private const val STATS_PATH = "user/partial"
private const val FRIEND_LIST_PATH = "user/friends"
private const val GAME_HISTORY_PATH = "user/game_history"

class OfflineUser(private val context: Context) : User {
    private lateinit var partialUser: PartialUser

    private lateinit var achievements: MutableList<Achievement>
    private lateinit var stats: MutableList<Statistic>
    private var friendsList: MutableList<PartialUser> = mutableListOf()

    private lateinit var gameHistory: MutableList<History>

    init {
        initUser()
        Thread.sleep(3000)
    }

    override fun getPartialUser(): PartialUser {
        return readFile(PARTIAL_USER_PATH) as PartialUser
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addAchievement(ach: Achievement) {
        achievements.add(ach)
        writeToFile(
            ACHIEVEMENT_PATH,
            ach as Serializable
        )
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun updateStats(statName: String, newValue: Long) {
        for (i in 0..stats.size) {
            if (statName == stats[i].name) {
                stats[i].value = newValue
                writeToFile(STATS_PATH, stats[i] as Serializable)
                return
            }
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addFriend(partialU: PartialUser) {
        if (!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
            writeToFile(FRIEND_LIST_PATH, friendsList as Serializable)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun removeFriend(partialU: PartialUser) {
        if (containsPartialUser(friendsList, partialU)) {
            friendsList.remove(partialU)
            writeToFile(FRIEND_LIST_PATH, friendsList as Serializable)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addGameInHistory(map: String, date: String, result: String) {
        val history = History(map, date, result)
        gameHistory.add(history)
        writeToFile(GAME_HISTORY_PATH, gameHistory as Serializable)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun changeUsername(newName: String) {
        partialUser.username = newName
        writeToFile(PARTIAL_USER_PATH, partialUser as Serializable)
    }

    override fun removeUserFromDatabase() {
        deleteContentFile(PARTIAL_USER_PATH)
        deleteContentFile(ACHIEVEMENT_PATH)
        deleteContentFile(STATS_PATH)
        deleteContentFile(FRIEND_LIST_PATH)
        deleteContentFile(GAME_HISTORY_PATH)
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
        val otherUser = other as OfflineUser
        return partialUser == otherUser.getPartialUser()
    }

    override fun hashCode(): Int {
        return partialUser.hashCode()
    }

    /*
     * Private methods to initialize the User
     */


    private fun initUser() {
        initializeStats()
        initializeAchievements()
        initializeFriendsList()
        initializeGameHistory()
        initializePartialUser()
    }

    private fun initializePartialUser() {
        val riddenUser: PartialUser? = readFile(PARTIAL_USER_PATH) as PartialUser?
        partialUser = riddenUser ?: PartialUser("defaultName", "dummy_id")
    }

    @Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST")
    private fun initializeAchievements() {
        val riddenAchievements: MutableList<Achievement>? =
            readFile(ACHIEVEMENT_PATH) as MutableList<Achievement>?
        achievements = riddenAchievements ?: mutableListOf(
            Achievement(
                "Create your account !",
                getCurrentDate()
            )
        )
        if (riddenAchievements == null) {
            writeToFile(ACHIEVEMENT_PATH, achievements as Serializable)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeStats() {
        val riddenStats: MutableList<Statistic>? = readFile(STATS_PATH) as MutableList<Statistic>?
        stats = riddenStats ?: mutableListOf(
            Statistic(
                "stat1",
                0
            ), Statistic("stat2", 0)
        )

    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeFriendsList() {
        val riddenFriendsList: MutableList<PartialUser>? =
            readFile(FRIEND_LIST_PATH) as MutableList<PartialUser>?
        friendsList = riddenFriendsList ?: mutableListOf(
            PartialUser(
                "dummy_friend_username",
                "dummy_friend_id"
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeGameHistory() {
        val riddenGameHistory: MutableList<History>? =
            readFile(GAME_HISTORY_PATH) as MutableList<History>?
        gameHistory = riddenGameHistory ?: mutableListOf(
            History(
                "dummy_map", getCurrentDate(), "VICTORY"
            )
        )

    }

    /*
    * Utility functions for this class
    */

    private fun writeToFile(path: String, serializable: Serializable) {
        //Create cache file in the cachedFile directory
        File.createTempFile(path, null, context.cacheDir)

        //Open the file and write the serializable object
        val cachedFile = File(context.cacheDir, path)
        cachedFile.printWriter().print(Json.encodeToString(serializable))
    }

    private fun readFile(path: String): Any? {
        //Get the cached file
        val cachedFile = File(context.cacheDir, path)

        //Read it
        val jsonFormatObject = cachedFile.bufferedReader().use { it.readText() }

        //Deserialize the object
        return if (jsonFormatObject.isEmpty()) {
            null
        } else {
            Json.decodeFromString(jsonFormatObject)
        }
    }

    private fun deleteContentFile(path: String) {
        //Get the cached file
        val cachedFile = File(context.cacheDir, path)

        //Delete it
        cachedFile.delete()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
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
}