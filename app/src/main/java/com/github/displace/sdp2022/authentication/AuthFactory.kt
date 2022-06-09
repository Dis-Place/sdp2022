package com.github.displace.sdp2022.authentication

import android.content.Intent

/**
 * used to retrieve appropriate
 * authentication mechanism
 */
object AuthFactory {

    const val MOCK_AUTH_EXTRA_ID = "MOCK_AUTH"

    lateinit var mockAuth : MockAuth
        private set

    /**
     * retrieve an Auth instance,
     *
     * @param intent specifies whether to use firebase or a mock
     * @return authentication mechanism instance
     */
    fun getAuth(intent: Intent): Auth {
        return if(intent.hasExtra(MOCK_AUTH_EXTRA_ID)) {
            if(!::mockAuth.isInitialized) {
                throw UnsupportedOperationException("Mock authentication has not been setup")
            }
            return mockAuth
        } else {
            FirebaseAuthAdapter
        }
    }

    /**
     * set username of mocked user and log out any logged in user
     *
     * @param username
     */
    fun setupMock(username: String) {
        mockAuth = MockAuth(username)
    }
}