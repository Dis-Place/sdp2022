package com.github.displace.sdp2022.unitTests

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule




class UsersTest {

    @Test
    fun partialUserEqualsWorksWhenTrue() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "dummy_id")
        Thread.sleep(3000)
        assertTrue(partialUser1.equals(partialUser2))
    }

    @Test
    fun partialUserEqualsWorksWhenFalse() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "other_id")
        Thread.sleep(3000)
        assertFalse(partialUser1.equals(partialUser2))
    }

    @Test
    fun completeUserEqualsWorksWhenTrue() {
        val completeUser1 = CompleteUser(null)
        val completeUser2 = CompleteUser(null)
        Thread.sleep(3000)
        assertTrue(completeUser1.equals(completeUser2))
    }

    @Test
    fun achievementUpdates() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val achSize = completeUser.getAchievements().size
        val ach = Achievement("AchievementTest1", "28-03-2022")
        completeUser.addAchievement(ach)
        val userAchievements = completeUser.getAchievements()[achSize]
        assertTrue(userAchievements.name == ach.name && userAchievements.date == ach.date)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun statisticsAreInitializedCorrectly() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val dummyStats = completeUser.getStats()
        assertTrue(dummyStats[0].name == "stat1" &&
                dummyStats[1].name == "stat2" &&
                0L == dummyStats[0].value &&
                0L == dummyStats[1].value)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun statisticsUpdates() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        completeUser.updateStats("stat1", 10)
        assertTrue(completeUser.getStats()[0].value == 10L)
        completeUser.updateStats("stat1", 0)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun addingAndRemovingFriendWorksCorrectly() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.addFriend(partialUser)
        assertEquals(partialUser, completeUser.getFriendsList()[friendsSize])
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun addExistingFriendDoesNothing() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_username", "dummy_id")
        completeUser.addFriend(partialUser)
        assertEquals(friendsSize,completeUser.getFriendsList().size)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun removeNonExistingFriendDoesNothing() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
        completeUser.removeUserFromDatabase()
    }

    @Test
    fun updateGameHistoryWorks() {
        val completeUser = CompleteUser(null)
        Thread.sleep(3000)
        val historySize = completeUser.getGameHistory().size
        val gameHistory = History("dummyMap", "28-03-2022", "VICTORY")
        completeUser.addGameInHistory("dummyMap", "28-03-2022", "VICTORY")
        val completeHistory = completeUser.getGameHistory()[historySize]
        assertTrue(completeHistory.map == gameHistory.map
                && completeHistory.date == gameHistory.date
                && completeHistory.result == gameHistory.result)
        completeUser.removeUserFromDatabase()
    }

}