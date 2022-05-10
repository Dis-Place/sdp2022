package com.github.displace.sdp2022.unitTest

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.github.displace.sdp2022.users.OfflineUserFetcher
import com.github.displace.sdp2022.users.PartialUser
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class OfflineUserFetcherTest {
    private var context: Context = getApplicationContext()

    @Before
    fun setup() {
        context.getSharedPreferences("cached-user", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun testOfflineEmptyStats() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val stats = mutableListOf<Statistic>()
        offlineUserFetcher.setOfflineStats(stats)
        assert(offlineUserFetcher.getOfflineStats() == stats)
    }

    @Test
    fun testOfflineEmptyAchievements() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val achievements = mutableListOf<Achievement>()
        offlineUserFetcher.setOfflineAchievements(achievements)
        assert(offlineUserFetcher.getOfflineAchievements() == achievements)
    }

    @Test
    fun testOfflineEmptyFriendsList() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val friendsList = mutableListOf<PartialUser>()
        offlineUserFetcher.setOfflineFriendsList(friendsList)
        assert(offlineUserFetcher.getOfflineFriendsList() == friendsList)
    }

    @Test
    fun testOfflineEmptyGameHistory() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val gameHistory = mutableListOf<History>()
        offlineUserFetcher.setOfflineGameHistory(gameHistory)
        assert(offlineUserFetcher.getOfflineGameHistory() == gameHistory)
    }

    @Test
    fun testOfflineNonEmptyStats() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val stats = mutableListOf(Statistic("Age of Bob", 12L), Statistic("Age of Alice", 13L))
        offlineUserFetcher.setOfflineStats(stats)
        assert(offlineUserFetcher.getOfflineStats() == stats)
    }

    @Test
    fun testOfflineNonEmptyAchievements() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val achievements = mutableListOf(
            Achievement("Spent 3 days in the lobby", date ="01-01-2001"),
            Achievement("Spent 10 days in the lobby", date ="11-01-2001")
        )
        offlineUserFetcher.setOfflineAchievements(achievements)
        assert(offlineUserFetcher.getOfflineAchievements() == achievements)
    }

    @Test
    fun testOfflineNonEmptyFriendsList() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val friendsList =
            mutableListOf(PartialUser("Bob", "bobsid"), PartialUser("Alice", "alicesid"))
        offlineUserFetcher.setOfflineFriendsList(friendsList)
        assert(offlineUserFetcher.getOfflineFriendsList() == friendsList)
    }

    @Test
    fun testOfflineNonEmptyGameHistory() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val gameHistory = mutableListOf(
            History("france", "01-01-2001", "LOSER"),
            History("baguettesland", "01-01-2001", "WINNER")
        )
        offlineUserFetcher.setOfflineGameHistory(gameHistory)
        assert(offlineUserFetcher.getOfflineGameHistory() == gameHistory)
    }

    @Test
    fun testEmptyStats() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        assert(
            offlineUserFetcher.getOfflineStats() == mutableListOf(
                Statistic(
                    "stat1",
                    0
                ),
                Statistic(
                    "stat2",
                    0
                )
            )
        )
    }

    @Test
    fun testEmptyAchievements() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        assert(
            offlineUserFetcher.getOfflineAchievements() == mutableListOf(
                Achievement(
                    "Create your account !",
                    date = SimpleDateFormat("dd-MM-yyyy").format(Date())
                )
            )
        )
    }

    @Test
    fun testEmptyFriendsList() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        assert(
            offlineUserFetcher.getOfflineFriendsList() == mutableListOf(
                PartialUser(
                    "dummy_friend_username",
                    "dummy_friend_id"
                )
            )
        )
    }

    @Test
    fun testEmptyGameHistory() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        assert(
            offlineUserFetcher.getOfflineGameHistory() == mutableListOf(
                History(
                    "dummy_map",
                    SimpleDateFormat("dd-MM-yyyy").format(Date()),
                    "VICTORY"
                )
            )
        )
    }

    @Test
    fun testOfflinePartialUserFetcher() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        val partialUser = PartialUser("Bob", "bobsid")
        offlineUserFetcher.setOfflinePartialUser(partialUser)
        assert(offlineUserFetcher.getOfflinePartialUser() == partialUser)
    }

    @Test
    fun testOfflinePartialUserFetcherWithEmptyPartialUser() {
        val offlineUserFetcher = OfflineUserFetcher(context)
        assert(offlineUserFetcher.getOfflinePartialUser() == PartialUser("defaultName", "dummy_id"))
    }

}
