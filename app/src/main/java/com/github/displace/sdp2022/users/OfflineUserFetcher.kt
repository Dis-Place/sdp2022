package com.github.displace.sdp2022.users

import android.content.Context
import android.util.Log
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.messages.Message
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.util.DateTimeUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.ArrayList

/**
 * Paths to the user informations in the local memory
 */
private const val PARTIAL_USER_PATH = "partial"
private const val ACHIEVEMENT_PATH = "achievements"
private const val STATS_PATH = "stats"
private const val FRIEND_LIST_PATH = "friends"
private const val GAME_HISTORY_PATH = "game_history"
private const val MESSAGE_HISTORY_PATH = "message_history"

/**
 * Class that can fetch all the user informations from the local cache
 * @param context Application context to
 */
class OfflineUserFetcher(private val context: Context?) {

    /**
     * Gets the cached Partial User
     */
    fun getOfflinePartialUser(): PartialUser {
        val partialUser: PartialUser? = readPreferences(PARTIAL_USER_PATH) as PartialUser?
        return partialUser ?: PartialUser("defaultName", "dummy_id")
    }

    /**
     * Gets the cached achievements
     */
    fun getOfflineAchievements(): MutableList<Achievement> {
        val offlineAchievements: MutableList<Achievement>? =
            readPreferences(ACHIEVEMENT_PATH) as MutableList<Achievement>?
        Log.e("debug", "offlineAchievements: $offlineAchievements")
        return offlineAchievements ?: mutableListOf(
            Achievement(
                "Welcome home!",
                "Create your account",
                DateTimeUtil.currentDate()
            )
        )
    }

    /**
     * Gets the cached statistics
     */
    fun getOfflineStats(): MutableList<Statistic> {
        val offlineStats: MutableList<Statistic>? =
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

    /**
     * Gets the cached friends' list
     */
    fun getOfflineFriendsList(): MutableList<PartialUser> {
        val offlineFriendsList: MutableList<PartialUser>? =
            readPreferences(FRIEND_LIST_PATH) as MutableList<PartialUser>?
        return offlineFriendsList ?: mutableListOf(
            PartialUser(
                "dummy_friend_username",
                "dummy_friend_id"
            )
        )
    }

    /**
     * Gets the cached game history
     */
    fun getOfflineGameHistory(): MutableList<History> {
        val offlineGameHistory: MutableList<History>? =
            readPreferences(GAME_HISTORY_PATH) as MutableList<History>?
        return offlineGameHistory ?: mutableListOf(
            History(
                "dummy_map", DateTimeUtil.currentDate(), "VICTORY"
            )
        )
    }

    /**
     * Gets the cached message history
     */
    fun getOfflineMessageHistory(): ArrayList<Message> {
        val offlineMessageHistory: ArrayList<Message>? =
            readPreferences(MESSAGE_HISTORY_PATH) as ArrayList<Message>?
        return offlineMessageHistory ?: arrayListOf()
    }

    /**
     * Saves a partial user locally
     * @param partialUser: Partial user to save locally
     */
    fun setOfflinePartialUser(partialUser: PartialUser?) {
        writeReference(PARTIAL_USER_PATH, partialUser)
    }

    /**
     * Saves a achievements' list locally
     * @param achievements: Achievements' list to save locally
     */
    fun setOfflineAchievements(achievements: MutableList<Achievement>?) {
        writeReference(ACHIEVEMENT_PATH, achievements ?: mutableListOf())
    }

    /**
     * Saves a list of statistics locally
     * @param stats: Statistics to save locally
     */
    fun setOfflineStats(stats: List<Statistic>?) {
        writeReference(STATS_PATH, stats ?: mutableListOf())
    }

    /**
     * Saves a friends' list locally
     * @param friendsList: Friends' list to save locally
     */
    fun setOfflineFriendsList(friendsList: MutableList<PartialUser>?) {
        writeReference(FRIEND_LIST_PATH, friendsList ?: mutableListOf())
    }

    /**
     * Saves a game history locally
     * @param gameHistory: Game history to save locally
     */
    fun setOfflineGameHistory(gameHistory: MutableList<History>?) {
        writeReference(GAME_HISTORY_PATH, gameHistory ?: mutableListOf())
    }

    /**
     * Saves a message history locally
     * @param messageHistory: Message history to save locally
     */
    fun setOfflineMessageHistory(messageHistory: ArrayList<Message>?) {
        writeReference(MESSAGE_HISTORY_PATH, messageHistory ?: arrayListOf())
    }

    /**
     * Saves an entire user locally
     * @param completeUser: User to save locally
     */
    fun setCompleteUser(completeUser: CompleteUser) {
        setOfflineAchievements(completeUser.getAchievements())
        setOfflineStats(completeUser.getStats())
        setOfflineFriendsList(completeUser.getFriendsList())
        setOfflineGameHistory(completeUser.getGameHistory())
        setOfflinePartialUser(completeUser.getPartialUser())
    }

    /**
     * Writes a serializable object to the local memory
     * @param path: Path to the local content
     * @param serializable: Objet to write to the local memory
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

    /**
     * Reads a serializable object from the local memory
     * @param path: Path to the local conten
     */
    private inline fun <reified T> readPreferences(path: String): T? {
        //Get the cached preference content reference
        val sharedPreferences =
            context?.getSharedPreferences("cached-user", Context.MODE_PRIVATE)

        //Read the content
        val jsonFormatObject = sharedPreferences?.getString(path, null)

        //Deserialize the object
        return if (jsonFormatObject == null || jsonFormatObject.isEmpty())
            null
        else
            Json.decodeFromString<T>(jsonFormatObject)

    }
}