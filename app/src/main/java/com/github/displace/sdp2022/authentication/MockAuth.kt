package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Mock Authentication mechanism
 *
 * @param credentialsUsername username of logged-in user,
 * when using signInWithCredential()
 */
class MockAuth(private val credentialsUsername: String?) : Auth {
    private var currentUser: MockAuthenticatedUser? = null

    override fun currentUser(): AuthenticatedUser? {
        return currentUser
    }

    override fun signInAnonymously(): Task<AuthResult> {
        currentUser = MockAuthenticatedUser(Random.nextUInt().toString(),null, this)
        return MockTask(MockAuthResult())

    }

    override fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
        currentUser = MockAuthenticatedUser(Random.nextUInt().toString(),credentialsUsername, this)
        return MockTask(MockAuthResult())
    }

    fun signOut() {
        currentUser = null
    }

}