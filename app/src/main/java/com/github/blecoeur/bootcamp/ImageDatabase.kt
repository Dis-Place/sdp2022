package com.github.blecoeur.bootcamp

import android.app.AlertDialog
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class ImageDatabase : Database {
    private lateinit var storage: FirebaseStorage
    private lateinit var imageRef: StorageReference

    override fun instantiate(url: String): Database {
        storage = Firebase.storage(url)
        imageRef = storage.reference.child("images")
        return this
    }

    override fun update(reference: String, key: String, obj: Object): Object {
        return insert(reference, key, obj)
    }

    override fun insert(reference: String, key: String, obj: Object): Object {
        val imageView: ImageView = obj as ImageView

        var file = Uri.fromFile(File(reference))

        var uploadTask = imageRef.child(key).putFile(file).addOnSuccessListener {
            Log.i("firebase", "Uploaded image $key")
        }.addOnFailureListener {
            Log.e("firebase", "Failed to upload image $key", it)
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(imageView.context)
            with(alertDialogBuilder) {
                setTitle("Error uploading the image")
                setNeutralButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        return imageView as Object
    }

    override fun delete(reference: String, key: String) {
        imageRef.child(reference).child(key).delete().addOnSuccessListener {
            Log.i("firebase", "Successfully deleted image $key")
        }.addOnFailureListener {
            Log.e("firebase", "Error while deleting image $key", it)
        }
    }

    @Deprecated("Not enough specific", ReplaceWith("getOnLocalFile or getOnMemory"))
    override fun get(reference: String, key: String): Object? {
        return getOnLocalFile(reference, key)
    }

    fun getOnMemory(reference: String, key: String): Object? {
        val TWENTY_MEGA_BYTE: Long = 20 * 1024 * 1024
        val uploadTask = imageRef.child(reference).getBytes(TWENTY_MEGA_BYTE).addOnSuccessListener {
            Log.i("firebase", "Successfully downloaded image $key")
        }.addOnFailureListener {
            Log.e("firebase", "Error while downloading the image $key", it)
        }

        return uploadTask.getResult() as Object
    }

    fun getOnLocalFile(reference: String, key: String): Object? {
        val localFile = File.createTempFile(key, "jpg")
        val uploadTask = imageRef.child(reference).getFile(localFile).addOnSuccessListener {
            Log.i("firebase", "Image $key was successfully downloaded ")
        }.addOnFailureListener {
            Log.e("firebase", "Error whole downloading image $key", it)
        }
        return localFile as Object
    }

}