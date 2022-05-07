package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val PARTIAL_USER_PATH = "partial"
private const val ACHIEVEMENT_PATH = "achievements"
private const val STATS_PATH = "stats"
private const val FRIEND_LIST_PATH = "friends"
private const val GAME_HISTORY_PATH = "game_history"
private const val MESSAGE_HISTORY_PATH = "message_history"

class OfflineUserFetcher(private val context: Context?) {

    /*
     * Private methods to initialize the User
     */
    fun getOfflinePartialUser(): PartialUser {
        val partialUser: PartialUser? = readPreferences(PARTIAL_USER_PATH) as PartialUser?
        return partialUser ?: PartialUser("defaultName", "dummy_id")
    }

    fun getOfflineAchievements(): MutableList<Achievement> {
        var offlineAchievements: MutableList<Achievement>? =
            readPreferences(ACHIEVEMENT_PATH) as MutableList<Achievement>?
        Log.e("debug", "offlineAchievements: $offlineAchievements")
        return offlineAchievements ?: mutableListOf(
            Achievement(
                "Create your account !",
                getCurrentDate()
            )
        )
    }

    fun getOfflineStats(): MutableList<Statistic> {
        var offlineStats: MutableList<Statistic>? =
            readPreferences(STATS_PATH) as MutableList<Statistic>?
        return offlineStats ?: mutableListOf(
            Statistic(
                "stat1",
                0
            ),
            Statistic(
                "stat2",
                0
            )
        )
    }

    fun getOfflineFriendsList(): MutableList<PartialUser> {
        var offlineFriendsList: MutableList<PartialUser>? =
            readPreferences(FRIEND_LIST_PATH) as MutableList<PartialUser>?
        return offlineFriendsList ?: mutableListOf(
            PartialUser(
                "dummy_friend_username",
                "dummy_friend_id"
            )
        )
    }

    fun getOfflineGameHistory(): MutableList<History> {
        var offlineGameHistory: MutableList<History>? =
            readPreferences(GAME_HISTORY_PATH) as MutableList<History>?
        return offlineGameHistory ?: mutableListOf(
            History(
                "dummy_map", getCurrentDate(), "VICTORY"
            )
        )
    }

    fun getOfflineMessageHistory(): ArrayList<Message> {
        var offlineMessageHistory: ArrayList<Message>? =
            readPreferences(MESSAGE_HISTORY_PATH) as ArrayList<Message>?
        return offlineMessageHistory ?: arrayListOf()
    }

    fun setOfflinePartialUser(partialUser: PartialUser?) {
        writeReference(PARTIAL_USER_PATH, partialUser)
    }

    fun setOfflineAchievements(achievements: MutableList<Achievement>?) {
        writeReference(ACHIEVEMENT_PATH, achievements ?: mutableListOf())
    }

    fun setOfflineStats(stats: List<Statistic>?) {
        writeReference(STATS_PATH, stats ?: mutableListOf())
    }

    fun setOfflineFriendsList(friendsList: MutableList<PartialUser>?) {
        writeReference(FRIEND_LIST_PATH, friendsList ?: mutableListOf())
    }

    fun setOfflineGameHistory(gameHistory: MutableList<History>?) {
        writeReference(GAME_HISTORY_PATH, gameHistory ?: mutableListOf())
    }

    fun setOfflineMessageHistory(messageHistory: ArrayList<Message>?) {
        writeReference(MESSAGE_HISTORY_PATH, messageHistory ?: arrayListOf())
    }

    fun setCompleteUser(completeUser: CompleteUser) {
        setOfflineAchievements(completeUser.getAchievements())
        setOfflineStats(completeUser.getStats())
        setOfflineFriendsList(completeUser.getFriendsList())
        setOfflineGameHistory(completeUser.getGameHistory())
        setOfflinePartialUser(completeUser.getPartialUser())
    }

    fun getCompleteUser(): CompleteUser {
        val completeUser = CompleteUser(context, null, offlineMode = true)
        completeUser.setCompleteUser(
            getOfflinePartialUser(),
            getOfflineAchievements(),
            getOfflineStats(),
            getOfflineFriendsList(),
            getOfflineGameHistory()
        )
        return completeUser
    }

    /*
    * Utility functions for this class
    */

    private inline fun <reified T> writeReference(path: String, serializable: T?) {
        if (serializable == null)
            return

        //Get the cached preference content reference
        val sharedPreferences =
            context?.getSharedPreferences("cached-user", Context.MODE_PRIVATE)
        //Write to the reference the serialized object

        sharedPreferences?.edit()?.putString(path, Json.encodeToString(serializable))?.apply()
    }

    private inline fun <reified T> readPreferences(path: String): T? {
        Log.e("debug", "Reading from $path")
        //Get the cached preference content reference
        val sharedPreferences =
            context?.getSharedPreferences("cached-user", Context.MODE_PRIVATE)
        //Read it
        val jsonFormatObject = sharedPreferences?.getString(path, null)
        Log.e("debug", "Read from $path: $jsonFormatObject")
        //Deserialize the object
        return if (jsonFormatObject == null || jsonFormatObject.isEmpty())
            null
        else
            Json.decodeFromString<T>(jsonFormatObject)

    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        return simpleDate.format(Date())
    }
}