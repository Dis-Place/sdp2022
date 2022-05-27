package com.github.displace.sdp2022.database

import android.net.Uri
import java.io.File

/**
 * Mock File Storage with constant content
 *
 * @property file
 */
class MockFileStorage(private val file: File): FileStorage {

    /*
    content never changes, so nothing happens
    */
    override fun put(uri: Uri, onSuccess: () -> Unit, onFailure: () -> Unit) {
        onSuccess()
    }

    override fun getThenCall(destination: File, onSuccess: () -> Unit, onFailure: () -> Unit) {
        try {
            destination.writeBytes(file.readBytes())
            onSuccess()
        } catch (e: Exception) {
            onFailure()
        }
    }

    /*
    content never changes, so nothing happens
    */
    override fun clear() {}
}