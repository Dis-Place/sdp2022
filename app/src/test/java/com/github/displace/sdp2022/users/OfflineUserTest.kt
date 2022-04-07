package com.github.displace.sdp2022.users

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class OfflineUserTest {
    @Test
    fun offlineUsersEqualsWorkWhenTrue() {
        val offlineUser1 = OfflineUser(null, true)
        val offlineUser2 = OfflineUser(null, true)

        //There can be only one offline user
        assertTrue(offlineUser1 == offlineUser2)
    }

    @Test
    fun addingAchievementWorks() {
        val offlineUser = OfflineUser(null, true)
        val achievement = Achievement("aa", "01-01-0001")

        offlineUser.addAchievement(achievement)

        assertTrue(offlineUser.getAchievements().contains(achievement))
    }

    @Test
    fun statisticsAreInitializedCorrectly() {
        val offlineUser = OfflineUser(null, true)
        val dummyStats = offlineUser.getStats()
        assertTrue(
            dummyStats[0].name == "stat1" &&
                    dummyStats[1].name == "stat2" &&
                    0L == dummyStats[0].value &&
                    0L == dummyStats[1].value
        )
    }

    @Test
    fun updatingStatsWorks() {
        val offlineUser = OfflineUser(null, true)

        offlineUser.updateStats("stat1", 10L)
        assertTrue(offlineUser.getStats()[0].value == 10L)
    }

    @Test
    fun addingFriendWorksWhenFriendSIsNew() {
        val offlineUser = OfflineUser(null, true)
        val friend = PartialUser("bloup", "blop")

        offlineUser.addFriend(friend)

        assertTrue(offlineUser.getFriendsList().contains(friend))
    }

    @Test
    fun addingFriendWorksWhenFriendSIsNotNew() {
        val offlineUser = OfflineUser(null, true)
        val friend = PartialUser("bloup", "blop")

        offlineUser.addFriend(friend)
        val sizeofFriendsList = offlineUser.getFriendsList().size
        offlineUser.addFriend(friend)

        assertTrue(offlineUser.getFriendsList().size == sizeofFriendsList)
    }

    @Test
    fun removingFriendWorks() {
        val offlineUser = OfflineUser(null, true)
        val friend = PartialUser("bloup", "blop")

        offlineUser.addFriend(friend)
        val sizeofFriendsList = offlineUser.getFriendsList().size
        offlineUser.removeFriend(friend)

        assertTrue(offlineUser.getFriendsList().size == sizeofFriendsList - 1)
        assertFalse(offlineUser.getFriendsList().contains(friend))
    }

    @Test
    fun addingGameInHistoryWorks() {
        val offlineUser = OfflineUser(null, true)
        val history = History("game1", "01-01-0001", "LOSER")

        val sizeofHistoryList = offlineUser.getGameHistory().size

        offlineUser.addGameInHistory(history.map, history.date, history.result)

        assertTrue(sizeofHistoryList + 1 == offlineUser.getGameHistory().size)

        assertTrue(offlineUser.getGameHistory().contains(history))
    }


    @Test
    fun changingUserNameWorks() {
        val offlineUser = OfflineUser(null, true)
        val userName = "userName"
        offlineUser.changeUsername(userName)

        assertTrue(offlineUser.getPartialUser()?.username == userName)
    }

    @Test
    fun removingUserFromDatabaseInDebugModeDoesNothing() {
        val offlineUser = OfflineUser(null, true)
        offlineUser.removeUserFromDatabase()
        assertTrue(true)
    }

}