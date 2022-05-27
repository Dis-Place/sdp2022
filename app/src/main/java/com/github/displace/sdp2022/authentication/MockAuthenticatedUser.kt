package com.github.displace.sdp2022.authentication
import com.google.android.gms.tasks.Task


/**
 * mock Authenticated User
 *
 * @property uid
 * @property displayName
 * @property mockAuth
 *
 * @author LeoLgdr
 */
class MockAuthenticatedUser(private val uid: String, private val displayName: String?, private val mockAuth: MockAuth): AuthenticatedUser {
    override fun uid(): String {
        return uid
    }

    override fun displayName(): String? {
        return displayName
    }

    override fun delete(): Task<Void> {
        mockAuth.signOut()
        return MockTask(null)
    }
}