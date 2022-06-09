package com.github.displace.sdp2022.database

import android.net.Uri
import java.io.File

/**
 * abstraction of the storage of exactly one file
 */
interface FileStorage {

    /**
     * put file in storage
     *
     * @param uri
     */
    fun put(uri: Uri, onSuccess: () -> Unit, onFailure: () -> Unit)

    /**
     * retrieve the file
     *
     * @param destination
     * @param onSuccess
     * @param onFailure
     */
    fun getThenCall(destination: File, onSuccess: () -> Unit, onFailure: () -> Unit)

    /**
     * delete stored file
     */
    fun clear()

}