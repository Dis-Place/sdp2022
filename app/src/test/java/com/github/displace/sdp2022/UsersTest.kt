package com.github.displace.sdp2022

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsersTest {
    @Test
    fun partialUserEqualsWorksWhenTrue() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "dummy_id")

        assertTrue(partialUser1==partialUser2)
    }

    @Test
    fun partialUserEqualsWorksWhenFalse() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "other_id")

        assertFalse(partialUser1==partialUser2)
    }

    @Test
    fun achievementUpdates() {
        val completeUser = CompleteUser(null)
        val ach = Achievement("AchievementTest1", "28-03-2022")
        completeUser.addAchievement(ach)
        val userAchievements = completeUser.getAchievements()[0]
        assertTrue(userAchievements.name == ach.name && userAchievements.date == ach.date)
    }

    @Test
    fun statisticsAreInitializedCorrectly() {
        val completeUser = CompleteUser(null)
        val dummyStats = completeUser.getStats()
        assertTrue(dummyStats[0].name == "stat1"
                && dummyStats[1].name == "stat2"
                && dummyStats[0].value == 0
                && dummyStats[1].value == 0)
    }

    @Test
    fun statisticsUpdates() {
        val completeUser = CompleteUser(null)
        completeUser.updateStats("stat1", 10)
        assertTrue(completeUser.getStats()[0].value == 10)
    }

    @Test
    fun addingAndRemovingFriendWorksCorrectly() {
        val completeUser = CompleteUser(null)
        val partialUser = PartialUser("dummy_name", "dummy_id")
        completeUser.addFriend(partialUser)
        assertTrue(completeUser.getFriendsList()[0] == partialUser)
        completeUser.removeFriend(partialUser)
        assertTrue(completeUser.getFriendsList().size == 0)
    }

    @Test
    fun addExistingFriendDoesNothing() {
        val completeUser = CompleteUser(null)
        val partialUser = PartialUser("dummy_name", "dummy_id")
        completeUser.addFriend(partialUser)
        completeUser.addFriend(PartialUser("dummydummy_name", "dummy_id"))
        assertTrue(completeUser.getFriendsList().size == 1)
    }

    @Test
    fun removeNonExistingFriendDoesNothing() {
        val completeUser = CompleteUser(null)
        val partialUser = PartialUser("dummy_name", "dummy_id")
        completeUser.removeFriend(partialUser)
        assertTrue(completeUser.getFriendsList().size == 0)
    }

    @Test
    fun updateGameHistoryWorks() {
        val completeUser = CompleteUser(null)
        val gameHistory = History("dummyMap", "28-03-2022", "VICTORY")
        completeUser.addGameInHistory("dummyMap", "28-03-2022", "VICTORY")
        val completeHistory = completeUser.getGameHistory()[0]
        assertTrue(completeHistory.map == gameHistory.map
                && completeHistory.date == gameHistory.date
                && completeHistory.result == gameHistory.result)
    }

}