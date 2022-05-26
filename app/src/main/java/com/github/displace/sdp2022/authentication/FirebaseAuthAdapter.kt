package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuthAdapter: Auth {
    override fun currentUser(): AuthenticatedUser? {
        val firebaseUser = Firebase.auth.currentUser ?: return null
        return FirebaseUserAdapter(firebaseUser)
    }

    override fun signInAnonymously(): Task<AuthResult> {
        return Firebase.auth.signInAnonymously()
    }

    override fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
        return Firebase.auth.signInWithCredential(credential)
    }

}