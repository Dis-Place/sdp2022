package com.github.displace.sdp2022.authentication

import com.google.android.gms.tasks.Task

/**
 * represents basic information
 * about the current logged-in user
 */
interface AuthenticatedUser {
    /**
     * @return unique user id
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