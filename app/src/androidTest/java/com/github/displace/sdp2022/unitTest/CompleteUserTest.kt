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

    @Test
    fun completeUserOfflineModeWorks() {
        DatabaseFactory.clearMockDB()
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB, offlineMode = true)
        checkThatUserIsReadOnly(completeUser)
    }

    /**
     * Checks that the user can't update any of its info
     * In offline mode, user shouldn't be able to update anything
     * @param completeUser: The user
     */
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
        completeUser.addFriend(PartialUser("friend1", "aaaa"), false)
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
    fun partialUserEqualsWorksWhenTrue() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "dummy_id")
        assertTrue(partialUser1 == partialUser2)
    }

    @Test
    fun partialUserEqualsWorksWhenFalse() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "other_id")
        assertFalse(partialUser1 == partialUser2)
    }

    @Test
    fun completeUserEqualsWorksWhenTrue() {
        val completeUser1 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val completeUser2 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        assertTrue(completeUser1 == completeUser2)
    }

    @Test
    fun achievementUpdates() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val achSize = completeUser.getAchievements().size
        val ach = Achievement("AchievementTest1", date = "28-03-2022")
        completeUser.addAchievement(ach)
        val userAchievements = completeUser.getAchievements()[achSize]
        assertTrue(userAchievements.name == ach.name && userAchievements.date == ach.date)
    }

    @Test
    fun statisticsAreInitializedCorrectly() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val dummyStats = completeUser.getStats()
        assertTrue(
            dummyStats[0].name == "Games Played" &&
                    dummyStats[1].name == "Games Won" &&
                    0L == dummyStats[0].value &&
                    0L == dummyStats[1].value
        )
    }

    @Test
    fun statisticsUpdates() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        completeUser.updateStats("Games Played", 10)
        assertTrue(completeUser.getStats()[0].value == 10L)
    }

    @Test
    fun addingAndRemovingFriendWorksCorrectly() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.addFriend(partialUser, false)
        assertEquals(partialUser, completeUser.getFriendsList()[friendsSize])
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
    }

    @Test
    fun addExistingFriendDoesNothing() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val partialUser = PartialUser("dummy_username", "dummy_id")
        completeUser.addFriend(partialUser, false)
        val friendsSize = completeUser.getFriendsList().size
        completeUser.addFriend(partialUser, false)

        assertEquals(friendsSize, completeUser.getFriendsList().size)
    }

    @Test
    fun removeNonExistingFriendDoesNothing() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val friendsSize = completeUser.getFriendsList().size
        val partialUser = PartialUser("dummy_name", "dummy_other_id")
        completeUser.removeFriend(partialUser)
        assertEquals(friendsSize, completeUser.getFriendsList().size)
    }

    @Test
    fun updateGameHistoryWorks() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, null, DatabaseFactory.MOCK_DB)
        val historySize = completeUser.getGameHistory().size
        val gameHistory = History("dummyMap", "28-03-2022", "VICTORY")
        completeUser.addGameInHistory("dummyMap", "28-03-2022", "VICTORY")
        val completeHistory = completeUser.getGameHistory()[historySize]
        assertTrue(
            completeHistory.gameMode == gameHistory.gameMode
                    && completeHistory.date == gameHistory.date
                    && completeHistory.result == gameHistory.result
        )
    }

    @Test
    fun hashCodeWorks() {
        val completeUser = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication,null, DatabaseFactory.MOCK_DB)
        val hashCode = completeUser.hashCode()
        assertTrue(hashCode != 0)
    }

}