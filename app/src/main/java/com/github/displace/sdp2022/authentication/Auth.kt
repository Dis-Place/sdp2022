package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

/**
 * englobes supported authentication mechanisms
 *
 * @author LeoLgdr
 */
interface Auth { 

    /**
     * @return currently logged user, or null if no user is logged in
     */
    fun currentUser(): AuthenticatedUser?

    /**
     * sign in as a guest
     */
    fun signInAnonymously(): Task<AuthResult>

    /**
     * sign in with a credential (usually google)
     * @param credential
     */
    fun signInWithCredential(credential: AuthCredential): Task<AuthResult>
}