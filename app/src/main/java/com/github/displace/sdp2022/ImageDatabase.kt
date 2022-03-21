package com.github.displace.sdp2022

import android.graphics.Bitmap
import com.github.displace.sdp2022.util.math.Constants.TWENTY_MEGA_BYTE
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ImageDatabase : Database {
    private lateinit var storage: FirebaseStorage
    private lateinit var imageRef: StorageReference

    private fun getChild(key: String): StorageReference {
        return imageRef.child(key)
    }

    override fun instantiate(url: String): com.github.displace.sdp2022.Database {
        storage = Firebase.storage(url)
        imageRef = storage.reference.child("images")
        return this
    }

    override fun update(reference: String, key: String, obj: Any): Any {
        return insert(reference, key, obj)
    }

    override fun insert(reference: String, key: String, obj: Any): Any {
        val bitmap = obj as Bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = getChild(key).putBytes(data).addOnFailureListener {
            throw IOException()
        }
        return uploadTask
    }

    override fun delete(reference: String, key: String) {
        getChild(reference).child(key).delete()
    }

    @Deprecated("Not enough specific", ReplaceWith("getOnLocalFile or getOnMemory"))
    override fun get(reference: String, key: String): Any {
        return getOnLocalFile(reference, key)
    }

    fun getOnMemory(reference: String, key: String): Any {
        val uploadTask = getChild(reference).getBytes(TWENTY_MEGA_BYTE)

        return uploadTask.result as Any
    }

    fun getOnLocalFile(reference: String, key: String): Any {
        val localFile = File.createTempFile(key, "jpg")

        getChild(reference).getFile(localFile)
        return localFile as Any
    }

}