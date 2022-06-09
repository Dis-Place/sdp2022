package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

/**
 * adapter of firebaseUser to our AuthenticatedUser interface
 *
 * @property firebaseUser
 */
class FirebaseUserAdapter(private val firebaseUser: FirebaseUser) : AuthenticatedUser {
    override fun uid(): String {
        return firebaseUser.uid
    }

    override fun displayName(): String? {
        return firebaseUser.displayName
    }

    override fun delete(): Task<Void> {
        return firebaseUser.delete()
    }

}