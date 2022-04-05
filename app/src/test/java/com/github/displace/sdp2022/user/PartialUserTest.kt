package com.github.displace.sdp2022.user

import com.github.displace.sdp2022.users.PartialUser
import org.junit.Assert
import org.junit.Test

class PartialUserTest {
    @Test
    fun partialUserEqualsWorksWhenTrue() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "dummy_id")

        Assert.assertTrue(partialUser1 == partialUser2)
    }

    @Test
    fun partialUserEqualsWorksWhenFalse() {
        val partialUser1 = PartialUser("dummy_name", "dummy_id")
        val partialUser2 = PartialUser("dummy_name", "other_id")

        Assert.assertFalse(partialUser1 == partialUser2)
    }
}