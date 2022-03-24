package com.github.displace.sdp2022.users

import com.github.displace.sdp2022.profile.achievements.Achievement
import com.github.displace.sdp2022.profile.history.History
import com.github.displace.sdp2022.profile.statistics.Statistic
import com.google.firebase.auth.FirebaseUser

class CompleteUser(private val firebaseUser: FirebaseUser) {

    private val partialUser = if(firebaseUser.displayName == null) {        // maybe add the profile picture later
        PartialUser("defaultName", firebaseUser.uid)
    } else {
        PartialUser(firebaseUser.displayName!!, firebaseUser.uid)
    }

    private val achievements: MutableList<Achievement> = mutableListOf()
    private val stats: MutableList<Statistic> = initializeStats()
    private val friendsList: MutableList<PartialUser> = mutableListOf()

    private var gameHistory: MutableList<History> = mutableListOf()

    init {
        addUserToDatabase()
    }

    fun addUserToDatabase() {
        //TODO: Add User to Database when the Database is centralized
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
        if(!friendsList.contains(partialU)) {
            friendsList.add(partialU)
        }
    }

    fun removeFriend(partialU: PartialUser) {
        if(friendsList.contains(partialU)) {
            friendsList.add(partialU)
        }
    }

    fun addGameInHistory(map: String, date: String, result: String) {
        val history = History(map, date, result)
        gameHistory.add(history)
    }

    private fun initializeStats(): MutableList<Statistic> {
        return mutableListOf()              // It's an empty list for now, will be replaced with a list of all the possible statistics initialized to 0
    }

    fun getPartialUser(): PartialUser {
        return partialUser
    }

    override fun equals(other: Any?): Boolean {
        val otherUser = other as CompleteUser
        return partialUser == otherUser.getPartialUser()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}