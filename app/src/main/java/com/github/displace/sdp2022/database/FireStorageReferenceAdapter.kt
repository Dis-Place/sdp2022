package com.github.displace.sdp2022.database

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

/**
 * adapter of Firebase StorageReference
 * to interface FileStorage
 *
 * @property fileUrl
 * @constructor
 * TODO
 *
 * @param baseUrl
 */
class FireStorageReferenceAdapter(baseUrl: String, private val fileUrl: String) : FileStorage {
    private val storageReference = Firebase.storage(baseUrl).reference

    override fun put(uri: Uri) {
        storageReference.child(fileUrl).putFile(uri)
    }

    override fun getThenCall(destination: File, onSuccess: () -> Unit, onFailure: () -> Unit) {
        storageReference.child(fileUrl).getFile(destination).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    override fun clear() {
        storageReference.child(fileUrl).delete()
    }

}