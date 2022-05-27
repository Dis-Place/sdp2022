package com.github.displace.sdp2022.authentication

import android.content.Intent
import com.google.firebase.auth.EmailAuthProvider

/**
 * to mock authentication in tests
 *
 * @author LeoLgdr
 */
object MockAuthUtils {
    /**
     * specify in intent that a mock authentication mechanism
     * should be used
     *
     * @param intent
     * @param username name of the mocked user, if signed in with credential
     */
    fun mockIntent(intent: Intent) {
        intent.putExtra(AuthFactory.MOCK_AUTH_EXTRA_ID, "")
    }

    val mockAuthCredential = EmailAuthProvider.getCredential("dummy@email.com","pwd")
}