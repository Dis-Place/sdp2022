package com.github.displace.sdp2022.unitTest

import androidx.test.core.app.ApplicationProvider
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class CompleteUserTest {

    @Before
    fun before() {
        DatabaseFactory.clearMockDB()
    }

    private fun checkThatUserIsReadOnly(completeUser: CompleteUser) {
        val achievementsSizeBefore = completeUser.getAchievements().size
        completeUser.addAchievement(Achievement("aaa", date = "2020-01-01"))
        assertTrue(completeUser.getAchievements().size == achievementsSizeBefore)

        completeUser.updateStats("stats1", 99L)
        val stats = completeUser.getStats()
        for (i in stats.indices) {
            if ("stat1" == stats[i].name) {
                assertTrue(stats[i].value != 99L)
            }
        }

        val friendListBefore = completeUser.getFriendsList().size
        completeUser.addFriend(PartialUser("friend1", "aaaa"))
        assertTrue(completeUser.getFriendsList().size == friendListBefore)

        completeUser.removeFriend(PartialUser("friend1", "aaaa"))
        assertTrue(completeUser.getFriendsList().size == friendListBefore)

        val historySizeBefore = completeUser.getGameHistory().size
        completeUser.addGameInHistory("game1", "2020-01-01", "lost")
        assertTrue(completeUser.getGameHistory().size == historySizeBefore)

        val currentUserName = completeUser.getPartialUser().username
        completeUser.changeUsername("avatar")
        assertTrue(completeUser.getPartialUser().username == currentUserName)
    }

    @Test
    fun completeUserOfflineModeWorks() {
        DatabaseFactory.clearMockDB()
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB, offlineMode = true)
        //Thread.sleep(3_000)
        checkThatUserIsReadOnly(completeUser)
    }

    @Test
    fun partialUserEqualsWorksWhenTrue() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "dummy_id")
        //Thread.sleep(3000)
        assertTrue(partialUser1 == partialUser2)
    }

    @Test
    fun partialUserEqualsWorksWhenFalse() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "other_id")
        //Thread.sleep(3000)
        assertFalse(partialUser1 == partialUser2)
    }

    @Test
    fun completeUserEqualsWorksWhenTrue() {
        val completeUser1 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val completeUser2 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(6_000)
        assertTrue(completeUser1 == completeUser2)
        //completeUser1.removeUserFromDatabase()
    }

    @Test
    fun achievementUpdates() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val achSize = completeUser.getAchievements().size
        val ach = Achievement("AchievementTest1", date = "28-03-2022")
        completeUser.addAchievement(ach)
        val userAchievements = completeUser.getAchievements()[achSize]
        assertTrue(userAchievements.name == ach.name && userAchievements.date == ach.date)
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun statisticsAreInitializedCorrectly() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val dummyStats = completeUser.getStats()
        assertTrue(
            dummyStats[0].name == "Games Played" &&
                    dummyStats[1].name == "Games Won" &&
                    0L == dummyStats[0].value &&
                    0L == dummyStats[1].value
        )
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun statisticsUpdates() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        completeUser.updateStats("Games Played", 10)
        assertTrue(completeUser.getStats()[0].value == 10L)
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun addingAndRemovingFriendWorksCorrectly() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.addFriend(partialUser)
        assertEquals(partialUser, completeUser.getFriendsList()[friendsSize])
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun addExistingFriendDoesNothing() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val partialUser = PartialUser("dummy_username", "dummy_id")
        completeUser.addFriend(partialUser)
        val friendsSize = completeUser.getFriendsList().size
        completeUser.addFriend(partialUser)

        assertEquals(friendsSize, completeUser.getFriendsList().size)
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun removeNonExistingFriendDoesNothing() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun updateGameHistoryWorks() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val historySize = completeUser.getGameHistory().size
        val gameHistory = History("dummyMap", "28-03-2022", "VICTORY")
        completeUser.addGameInHistory("dummyMap", "28-03-2022", "VICTORY")
        val completeHistory = completeUser.getGameHistory()[historySize]
        assertTrue(
            completeHistory.map == gameHistory.map
                    && completeHistory.date == gameHistory.date
                    && completeHistory.result == gameHistory.result
        )
        //completeUser.removeUserFromDatabase()
    }

    @Test
    fun hashCodeWorks() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication,null, DatabaseFactory.MOCK_DB)
        //Thread.sleep(3000)
        val hashCode = completeUser.hashCode()
        assertTrue(hashCode != 0)
    }

}