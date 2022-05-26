package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task

/**
 * represents basic information
 * about the currently logged in user
 *
 * @author LeoLgdr
 */
interface AuthenticatedUser {
    /**
     * @return unique uid
     */
    fun uid() : String

    /**
     * @return name of usage
     */
    fun displayName(): String?

    /**
     * deletes the user from the auth (sign out)
     *
     * @return delete task
     */
    fun delete() : Task<Void>
}