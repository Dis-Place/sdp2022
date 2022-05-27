package com.github.displace.sdp2022.authentication

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockAuthTest {

    private val username = "default"

    private val mockAuthCredential = EmailAuthProvider.getCredential("dummy@email.com","pwd")

    @Before
    fun setup() {
        AuthFactory.setupMock(username)
    }

    @Test
    fun callBackCalledOnAnonymousSignIn() {
        var callBackCalled = false
        AuthFactory.mockAuth.signInAnonymously().addOnCompleteListener {
            callBackCalled = true
        }
        assertTrue(callBackCalled)
    }

    @Test
    fun callBackCalledOnCredentialsSignIn() {
        var callBackCalled = false
        AuthFactory.mockAuth.signInWithCredential(mockAuthCredential).addOnCompleteListener {
            callBackCalled = true
        }
        assertTrue(callBackCalled)
    }

}