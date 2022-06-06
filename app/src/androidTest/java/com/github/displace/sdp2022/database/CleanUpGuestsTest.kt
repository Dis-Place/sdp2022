package com.github.displace.sdp2022.database

import androidx.test.core.app.ApplicationProvider
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.authentication.AuthFactory
import com.github.displace.sdp2022.authentication.MockAuth
import com.github.displace.sdp2022.authentication.MockAuthenticatedUser
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextUInt

class CleanUpGuestsTest {
    private var saveThreshold = 0
    private lateinit var auth: MockAuth
    private lateinit var db: GoodDB


    @Before
    fun before() {
        saveThreshold = CleanUpGuests.getThreshold()
        CleanUpGuests.setThreshold(3)
        DatabaseFactory.clearMockDB()
        AuthFactory.setupMock("dummy")
        auth = AuthFactory.mockAuth
        db = DatabaseFactory.MOCK_DB
    }

    @After
    fun after() {
        CleanUpGuests.setThreshold(saveThreshold)
    }

    @Test
    fun guestsAboveThresholdAreCleanedUpAndGuestsAreCorrectlyIndexed() {
        auth.signInAnonymously()
        val guest1 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest2 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest3 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest4 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        db.getThenCall<Map<String, *>>("CompleteUsers") { usrs ->
            if(usrs != null) {
                val mapPUToIndex = mapOf(Pair(guest2.getPartialUser(), 2L), Pair(guest3.getPartialUser(), 1L), Pair(guest4.getPartialUser(), 0L))
                assertTrue(usrs.size == 3)
                for(id in usrs.keys) {
                    val guestDB = usrs[id] as Map<String, *>
                    val guestCompleteUserDB = guestDB["CompleteUser"] as Map<String, *>
                    val index = guestCompleteUserDB["guestIndex"] as Long
                    val pUserDB = guestCompleteUserDB["partialUser"] as Map<String, String>
                    val pUser = PartialUser(pUserDB["username"]!!, pUserDB["uid"]!!)
                    val verifyIndex = mapPUToIndex[pUser]
                    assertTrue(
                        if(verifyIndex == null) {
                            false
                        } else {
                            verifyIndex == index
                        }
                    )
                }
            }
        }
    }

    @Test
    fun normalUsersNotCleanedUp() {
        auth.signInAnonymously()
        val guest1 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        AuthFactory.setupMock("user1")
        auth = AuthFactory.mockAuth
        val user1 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, MockAuthenticatedUser(Random.nextUInt().toString(),"user1", auth), db)
        AuthFactory.setupMock("user2")
        auth = AuthFactory.mockAuth
        val user2 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, MockAuthenticatedUser(Random.nextUInt().toString(),"user2", auth), db)
        auth.signInAnonymously()
        val guest2 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest3 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest4 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)
        auth.signInAnonymously()
        val guest5 = CompleteUser(ApplicationProvider.getApplicationContext() as MyApplication, auth.currentUser(), db, true)

        db.getThenCall<Map<String, *>>("CompleteUsers") { usrs ->
            if(usrs != null) {
                val mapPUToIndex = mapOf(Pair(guest3.getPartialUser(), 2L), Pair(guest4.getPartialUser(), 1L), Pair(guest5.getPartialUser(), 0L))
                val userNameSet = mutableSetOf("user1", "user2")
                assertTrue(usrs.size == 5)
                for(id in usrs.keys) {
                    val guestDB = usrs[id] as Map<String, *>
                    val guestCompleteUserDB = guestDB["CompleteUser"] as Map<String, *>
                    val index = guestCompleteUserDB["guestIndex"] as Long?
                    val pUserDB = guestCompleteUserDB["partialUser"] as Map<String, String>
                    val name = pUserDB["username"]!!
                    val pUser = PartialUser(name, pUserDB["uid"]!!)
                    val verifyIndex = mapPUToIndex[pUser]
                    assertTrue(
                        if(userNameSet.contains(name)) {
                            userNameSet.remove(name)
                            true
                        } else {
                            if(verifyIndex == null) {
                                false
                            } else {
                                verifyIndex == index
                            }
                        }
                    )
                }
                assertTrue(userNameSet.isEmpty())
            }
        }
    }
}