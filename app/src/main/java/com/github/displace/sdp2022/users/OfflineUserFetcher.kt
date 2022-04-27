package com.github.displace.sdp2022.users

import android.annotation.SuppressLint
import android.content.Context
import androidx.versionedparcelable.ParcelImpl
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

class OfflineUserFetcher(private val context: Context?, private val debug: Boolean = false) {
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

    /*
     * Private methods to initialize the User
     */

    fun getOfflinePartialUser(): PartialUser {
        val partialUser: PartialUser? = readFile(PARTIAL_USER_PATH) as PartialUser?
        return partialUser ?: PartialUser("defaultName", "dummy_id")
    }

    fun getOfflineAchievements(): MutableList<Achievement> {
        var offlineAchievements: MutableList<Achievement>? = readFile(ACHIEVEMENT_PATH) as MutableList<Achievement>?

        if(offlineAchievements == null) {
            offlineAchievements = mutableListOf(
                Achievement(
                    "Create your account !",
                    getCurrentDate()
                )
            )
        }

        return offlineAchievements
    }

    fun getOfflineStats(): MutableList<Statistic> {
        var offlineStats: MutableList<Statistic>? = readFile(STATS_PATH) as MutableList<Statistic>?

        if(offlineStats == null) {
            offlineStats = mutableListOf(
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

        return offlineStats
    }

    fun getOfflineFriendsList(): MutableList<PartialUser> {
        var offlineFriendsList: MutableList<PartialUser>? = readFile(FRIEND_LIST_PATH) as MutableList<PartialUser>?

        if(offlineFriendsList == null) {
            offlineFriendsList = mutableListOf(
                PartialUser(
                    "dummy_friend_username",
                    "dummy_friend_id"
                )
            )
        }

        return offlineFriendsList
    }

    fun getOfflineGameHistory(): MutableList<History> {
        var offlineGameHistory: MutableList<History>? = readFile(GAME_HISTORY_PATH) as MutableList<History>?

        if(offlineGameHistory == null) {
            offlineGameHistory = mutableListOf(
                History(
                    "dummy_map", getCurrentDate(), "VICTORY"
                )
            )
        }

        return offlineGameHistory
    }

    fun setOfflinePartialUser(partialUser: PartialUser) {
        writeToFile(PARTIAL_USER_PATH, partialUser)
    }

    fun setOfflineAchievements(achievements: MutableList<Achievement>) {
        writeToFile(ACHIEVEMENT_PATH, Achievements(achievements))
    }

    fun setOfflineStats(stats: MutableList<Statistic>) {
        writeToFile(STATS_PATH, Statistics(stats))
    }

    fun setOfflineFriendsList(friendsList: MutableList<PartialUser>) {
        writeToFile(FRIEND_LIST_PATH, FriendList(friendsList))
    }

    fun setOfflineGameHistory(gameHistory: MutableList<History>) {
        writeToFile(GAME_HISTORY_PATH, Histories(gameHistory))
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
}