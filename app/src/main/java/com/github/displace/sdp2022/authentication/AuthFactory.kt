package com.github.displace.sdp2022.authentication

import android.content.Intent

object AuthFactory {

    const val MOCK_AUTH_EXTRA_ID = "MOCK_AUTH"

    fun getAuth(intent: Intent): Auth {
        return if(intent.hasExtra(MOCK_AUTH_EXTRA_ID)) {
            MockAuth(intent.getStringExtra(MOCK_AUTH_EXTRA_ID))
        } else {
            FirebaseAuthAdapter
        }
    }
}