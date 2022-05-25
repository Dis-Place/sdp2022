package com.github.displace.sdp2022.profile.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.displace.sdp2022.ImageDatabase
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.ProgressDialogsUtil
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File

class AccountSettingsActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_GALLERY_REQUEST = 300
        const val IMAGE_CAMERA_REQUEST = 400
    }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted: Boolean ->
            if (accepted) {
                selectPicFromGallery()
            } else {
                showToastText("Please enable Storage permissions")
            }
        }
    private val cameraPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            for ((_, v) in permissions.entries) {
                granted = granted && v
            }
            if (granted) {
                selectPicFromCamera()
            } else {
                showToastText("Please enable Camera & Storage permissions")
            }
        }

    private lateinit var username: TextView
    private lateinit var profilePic: ImageView
    private var imageUri: Uri = Uri.EMPTY

    private val storageReference = Firebase.storage.reference

    private lateinit var activeUser: CompleteUser

    private lateinit var imgDBReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        username = findViewById(R.id.username)
        profilePic = findViewById(R.id.profilePic)

        profilePic.setTag(R.id.profilePic, "defaultPicTag") // For testing


        val app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!

        // Reference to the profile pic in Firebase Storage
        imgDBReference = storageReference.child("images/profilePictures/${activeUser.getPartialUser().uid}")

        if(!activeUser.getPartialUser().equals(PartialUser("defaultName", "dummy_id"))) {
            if(activeUser.getProfilePic() == null) {
                // Temp file for the profile pic
                val localFile = File.createTempFile("profilePic", "jpg")

                // Gets profile pic from database
                ProgressDialogsUtil.showProgressDialog(this)
                imgDBReference.getFile(localFile).addOnSuccessListener {
                    // keep a copy of the profile pic in the case connection lost, and more efficient
                    val pic = BitmapFactory.decodeFile(localFile.absolutePath)
                    if(pic != null) {
                        activeUser.setProfilePic(pic)
                        ProgressDialogsUtil.dismissProgressDialog()
                    }
                    profilePic.setImageBitmap(activeUser.getProfilePic())
                }.addOnFailureListener{
                    showToastText("Failed to load profile pic")
                    ProgressDialogsUtil.dismissProgressDialog()
                }
            } else {
                profilePic.setImageBitmap(activeUser.getProfilePic())
            }
        }


        username.text = activeUser.getPartialUser().username

    }

    @Suppress("UNUSED_PARAMETER")
    fun selectPic(view : View) {
        val options = arrayOf("Camera", "Gallery")

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Pick Image From")
        dialogBuilder.setItems(options) { _, pos ->
            if (pos == 0) {
                if (cameraPermissions()) {
                    selectPicFromCamera()
                } else {
                    requestCameraPermissions()
                }

            } else if (pos == 1) {
                if (storagePermissions()) {
                    selectPicFromGallery()
                } else {
                    requestStoragePermissions()
                }
            }
        }
        dialogBuilder.create().show()
    }

    /**
     * Verifies if the application has the necessary permissions for the camera
     */
    private fun cameraPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestCameraPermissions() {
        cameraPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun selectPicFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAMERA_REQUEST)
    }

    /**
     * Verifies if the application has the necessary permissions for the gallery
     */
    private fun storagePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED))
    }

    private fun requestStoragePermissions() {
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    @Suppress("DEPRECATION")
    private fun selectPicFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_CAMERA_REQUEST) {
                profilePic.setImageURI(imageUri)
            } else if (requestCode == IMAGE_GALLERY_REQUEST) {
                imageUri = data?.data!!
                profilePic.setImageURI(data.data)
            }
            profilePic.setTag(R.id.profilePic, "modifiedTag") // For testing
            uploadProfilePhoto(imageUri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadProfilePhoto(imageUri: Uri?) {

        if (imageUri == null) {
            showToastText("Unable to upload profile photo")
        } else {
            imgDBReference.putFile(imageUri).addOnSuccessListener {
                showToastText("Profile pic uploaded")
            }.addOnFailureListener {
                showToastText("Fail to upload profile pic")
            }

        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun changeUsername(view: View) {
        val newView = LayoutInflater.from(this).inflate(R.layout.username_update_dialog, null)
        val newName = newView.findViewById<EditText>(R.id.updateUsername)
        val newNameButton = newView.findViewById<Button>(R.id.updateUsernameButton)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(newView)
        val dialogUsername = dialogBuilder.create()
        dialogUsername.show()

        newNameButton.setOnClickListener {
            val name = newName.text.toString().trim()

            dialogUsername.dismiss()

            if (checkIfEmpty(name)) {
                showToastText("New Username can't be empty")
            } else {
                activeUser.changeUsername(name)
                username.text = name
            }
        }
    }

    private fun checkIfEmpty(string: String): Boolean {
        return TextUtils.isEmpty(string)
    }

    private fun showToastText(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
}