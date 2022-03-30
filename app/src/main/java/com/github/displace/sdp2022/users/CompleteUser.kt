package com.github.displace.sdp2022.users

import android.widget.Toast
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CompleteUser(private val googleAccount: GoogleSignInAccount?) {

    private val db: RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/") as RealTimeDatabase

    private val partialUser: PartialUser = if(googleAccount != null) {
        if(googleAccount.displayName == null) {        // maybe add the profile picture later
            PartialUser("defaultName", googleAccount.id!!)
        } else {
            PartialUser(googleAccount.displayName!!, googleAccount.id!!)
        }
    } else {
        PartialUser("defaultName", "errorUid")
    }


    private val achievements: MutableList<Achievement> = mutableListOf()
    private val stats: MutableList<Statistic> = initializeStats()
    private val friendsList: MutableList<PartialUser> = mutableListOf()

    private var gameHistory: MutableList<History> = mutableListOf()

    private val dbReference: String = "CompleteUsers/${googleAccount?.id}"

    init {
        addUserToDatabase()
    }

    private fun addUserToDatabase() {
        if(googleAccount != null) {
            db.insert(dbReference, googleAccount.id!!, this)
            /*db.insert(dbReference, "achievements", achievements)
            db.insert(dbReference, "statistics", stats)
            db.insert(dbReference, "friends", friendsList)
            db.insert(dbReference, "gameHistory", gameHistory)
            db.insert(dbReference, "partialUser", partialUser)*/
        }
    }

    fun addAchievement(ach: Achievement) {
        achievements.add(ach)
    }

    fun updateStats(statName: String, newValue: Int) {
        for(s in stats) {
            if(statName == s.name) {
                s.value = newValue
                return
            }
        }
    }

    fun addFriend(partialU: PartialUser) {
        if(!containsPartialUser(friendsList, partialU)) {
            friendsList.add(partialU)
        }
    }

    fun removeFriend(partialU: PartialUser) {
        if(containsPartialUser(friendsList, partialU)) {
            friendsList.remove(partialU)
        }
    }

    /**
     * Check if a partial user is in a list, by using the user id to verify it
     */
    fun containsPartialUser(pUserList: List<PartialUser>, partialU: PartialUser): Boolean {
        for(f in pUserList) {
            if(f.uid == partialU.uid) {
                return true
            }
        }
        return false
    }

    fun addGameInHistory(map: String, date: String, result: String) {
        val history = History(map, date, result)
        gameHistory.add(history)
    }

    private fun initializeStats(): MutableList<Statistic> {
        return mutableListOf(
            Statistic("stat1", 0),
            Statistic("stat2", 0))              // It's a dummy list for now, will be replaced with a list of all the possible statistics initialized to 0
    }

    fun getPartialUser(): PartialUser {
        return partialUser
    }

    fun getAchievements(): MutableList<Achievement> {
        return achievements
    }

    fun getStats(): MutableList<Statistic> {
        return stats
    }

    fun getFriendsList(): MutableList<PartialUser> {
        return friendsList
    }

    fun getGameHistory(): MutableList<History> {
        return gameHistory
    }

    override fun equals(other: Any?): Boolean {
        val otherUser = other as CompleteUser
        return partialUser == otherUser.getPartialUser()
    }

    override fun hashCode(): Int {
        return partialUser.hashCode()
    }

}