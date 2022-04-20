package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

private const val PARTIAL_USER_PATH = "user/partial"
private const val ACHIEVEMENT_PATH = "user/partial"
private const val STATS_PATH = "user/partial"
private const val FRIEND_LIST_PATH = "user/friends"
private const val GAME_HISTORY_PATH = "user/game_history"
private const val RDM_PATH = "user/rdm"

class OfflineUser(private val context: Context?, private val debug: Boolean = false) : User {
    /**
     * Achievements
     *
     * A simple wrapper that allows use to serialize and deserialize list of achievements
     */
    @Serializable
    private data class Achievements(@Serializable(with = AchievementArraySerializer::class) var array: MutableList<Achievement>)

    private object AchievementArraySerializer : KSerializer<MutableList<Achievement>> {
        override fun deserialize(decoder: Decoder): MutableList<Achievement> {
            return decoder.decodeSerializableValue(ArraySerializer(Achievement.serializer()))
                .toMutableList()

        }

        override val descriptor: SerialDescriptor =
            ArraySerializer(Achievement.serializer()).descriptor

        override fun serialize(encoder: Encoder, value: MutableList<Achievement>) {
            encoder.encodeSerializableValue(
                ArraySerializer(Achievement.serializer()),
                value.toTypedArray()
            )
        }
    }

    /**
     * Statistics
     *
     * A simple wrapper that allows use to serialize and deserialize list of statistics
     */
    @Serializable
    private data class Statistics(@Serializable(with = StatsArraySerializer::class) var array: MutableList<Statistic>)

    private object StatsArraySerializer : KSerializer<MutableList<Statistic>> {
        override val descriptor: SerialDescriptor =
            ArraySerializer(Statistic.serializer()).descriptor

        override fun deserialize(decoder: Decoder): MutableList<Statistic> {
            return ArraySerializer(Statistic.serializer()).deserialize(decoder).toMutableList()
        }

        override fun serialize(encoder: Encoder, value: MutableList<Statistic>) {
            ArraySerializer(Statistic.serializer()).serialize(encoder, value.toTypedArray())
        }
    }

    /**
     * History
     *
     * A simple wrapper that allows use to serialize and deserialize list of history
     */
    @Serializable
    private data class Histories(@Serializable(with = HistoryArraySerializer::class) var array: MutableList<History>)

    private object HistoryArraySerializer : KSerializer<MutableList<History>> {
        override fun deserialize(decoder: Decoder): MutableList<History> {
            return decoder.decodeSerializableValue(ArraySerializer(History.serializer()))
                .toMutableList()
        }

        override val descriptor: SerialDescriptor =
            ArraySerializer(History.serializer()).descriptor

        override fun serialize(encoder: Encoder, value: MutableList<History>) {
            ArraySerializer(History.serializer()).serialize(encoder, value.toTypedArray())
        }
    }

    /**
     * Friend list
     *
     * A simple wrapper that allows use to serialize and deserialize list of friends
     */
    @Serializable
    private data class FriendList(@Serializable(with = FriendArraySerializer::class) var array: MutableList<PartialUser>)

    private object FriendArraySerializer : KSerializer<MutableList<PartialUser>> {
        override fun deserialize(decoder: Decoder): MutableList<PartialUser> {
            return decoder.decodeSerializableValue(ArraySerializer(PartialUser.serializer()))
                .toMutableList()
        }

        override val descriptor: SerialDescriptor =
            ArraySerializer(PartialUser.serializer()).descriptor

        override fun serialize(encoder: Encoder, value: MutableList<PartialUser>) {
            ArraySerializer(PartialUser.serializer()).serialize(encoder, value.toTypedArray())
        }
    }


    private lateinit var partialUser: PartialUser
    private lateinit var achievements: Achievements
    private lateinit var stats: Statistics
    private lateinit var friendsList: FriendList
    private lateinit var gameHistory: Histories

    init {
        initUser()
        if (!debug)
            Thread.sleep(3000)
    }

    override fun getPartialUser(): PartialUser? {
        return if (debug)
            partialUser
        else
            readFile(PARTIAL_USER_PATH) as PartialUser?
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addAchievement(ach: Achievement) {
        achievements.array.add(ach)
        writeToFile(
            ACHIEVEMENT_PATH,
            ach
        )
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun updateStats(statName: String, newValue: Long) {
        for (i in 0 until stats.array.size) {
            if (statName == stats.array[i].name) {
                stats.array[i].value = newValue
                writeToFile(STATS_PATH, stats.array[i])
                return
            }
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addFriend(partialU: PartialUser) {
        if (!containsPartialUser(friendsList.array, partialU)) {
            friendsList.array.add(partialU)
            writeToFile(FRIEND_LIST_PATH, friendsList)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun removeFriend(partialU: PartialUser) {
        if (containsPartialUser(friendsList.array, partialU)) {
            friendsList.array.remove(partialU)
            writeToFile(FRIEND_LIST_PATH, friendsList)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun addGameInHistory(map: String, date: String, result: String) {
        val history = History(map, date, result)
        gameHistory.array.add(history)
        writeToFile(GAME_HISTORY_PATH, gameHistory)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun changeUsername(newName: String) {
        partialUser.username = newName
        writeToFile(PARTIAL_USER_PATH, partialUser)
    }

    override fun removeUserFromDatabase() {
        deleteContentFile(PARTIAL_USER_PATH)
        deleteContentFile(ACHIEVEMENT_PATH)
        deleteContentFile(STATS_PATH)
        deleteContentFile(FRIEND_LIST_PATH)
        deleteContentFile(GAME_HISTORY_PATH)
    }

    override fun getAchievements(): MutableList<Achievement> {
        return achievements.array
    }

    override fun getStats(): List<Statistic> {
        return stats.array
    }

    override fun getFriendsList(): MutableList<PartialUser> {
        return friendsList.array
    }

    override fun getGameHistory(): MutableList<History> {
        return gameHistory.array
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
        achievements = Achievements(
            riddenAchievements ?: mutableListOf(
                Achievement(
                    "Create your account !",
                    getCurrentDate()
                )
            )
        )
        if (riddenAchievements == null) {
            writeToFile(ACHIEVEMENT_PATH, achievements)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeStats() {
        val riddenStats: MutableList<Statistic>? =
            readFile(STATS_PATH) as MutableList<Statistic>?
        stats = Statistics(
            riddenStats ?: mutableListOf(
                Statistic(
                    "stat1",
                    0
                ), Statistic("stat2", 0)
            )
        )

        if (riddenStats == null) {
            writeToFile(STATS_PATH, stats)
        }

    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeFriendsList() {
        val riddenFriendsList: MutableList<PartialUser>? =
            readFile(FRIEND_LIST_PATH) as MutableList<PartialUser>?
        friendsList = FriendList(
            riddenFriendsList ?: mutableListOf(
                PartialUser(
                    "dummy_friend_username",
                    "dummy_friend_id"
                )
            )
        )

        if (riddenFriendsList == null) {
            writeToFile(FRIEND_LIST_PATH, friendsList)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeGameHistory() {
        val riddenGameHistory: MutableList<History>? =
            readFile(GAME_HISTORY_PATH) as MutableList<History>?
        gameHistory = Histories(
            riddenGameHistory ?: mutableListOf(
                History(
                    "dummy_map", getCurrentDate(), "VICTORY"
                )
            )
        )

        if (riddenGameHistory == null) {
            writeToFile(GAME_HISTORY_PATH, gameHistory)
        }

    }

    /*
    * Utility functions for this class
    */

    private fun writeToFile(path: String, serializable: Any) {
        if (debug)
            return

        //Create cache file in the cachedFile directory
        File.createTempFile(path, null, context?.cacheDir ?: File(RDM_PATH))

        //Open the file and write the serializable object
        val cachedFile = File(context?.cacheDir ?: File(RDM_PATH), path)
        cachedFile.printWriter().print(Json.encodeToString(serializable))
    }

    private fun readFile(path: String): Any? {
        if (debug)
            return null

        //Get the cached file
        val cachedFile = File(context?.cacheDir ?: File(RDM_PATH), path)

        //Read it
        val jsonFormatObject =
            try {
                cachedFile.bufferedReader().use { it.readText() }
            } catch (e: FileNotFoundException) {
                return null
            }

        //Deserialize the object
        return if (jsonFormatObject.isEmpty()) {
            null
        } else {
            Json.decodeFromString(jsonFormatObject)
        }
    }

    private fun deleteContentFile(path: String) {
        if (debug)
            return


        //Get the cached file
        val cachedFile = File(context?.cacheDir ?: File(RDM_PATH), path)

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