package com.github.displace.sdp2022.unitTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.displace.sdp2022.users.OfflineUser
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineUserTest {
    @Test
    fun offlineUsersEqualsWorkWhenTrue() {
        val offlineUser1 = OfflineUser(null, true)
        val offlineUser2 = OfflineUser(null, true)

        //There can be only one offline user
        assertFalse(offlineUser1 == offlineUser2)
    }
}