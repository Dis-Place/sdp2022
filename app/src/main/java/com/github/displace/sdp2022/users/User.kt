package com.github.displace.sdp2022.users

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic

/**
 * A user of the system.
 */
interface User {
    fun addAchievement(ach: Achievement)

    fun updateStats(statName: String, newValue: Long)

    fun addFriend(partialU: PartialUser)

    fun removeFriend(partialU: PartialUser)

    fun addGameInHistory(map: String, date: String, result: String)

    fun changeUsername(newName: String)

    fun removeUserFromDatabase()

    fun getPartialUser(): PartialUser

    fun getAchievements(): MutableList<Achievement>

    fun getStats(): List<Statistic>

    fun getFriendsList(): MutableList<PartialUser>

    fun getGameHistory(): MutableList<History>
}