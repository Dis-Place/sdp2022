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
class FireStorageReferenceAdapter(private val fileUrl: String) : FileStorage {
    private val storageReference = Firebase.storage.reference

    override fun put(uri: Uri, onSuccess: () -> Unit, onFailure: () -> Unit) {
        storageReference.child(fileUrl).putFile(uri)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener{
                onFailure()
            }
    }

    override fun getThenCall(destination: File, onSuccess: () -> Unit, onFailure: () -> Unit) {
        storageReference.child(fileUrl).getFile(destination)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
    }

    override fun clear() {
        storageReference.child(fileUrl).delete()
    }

}