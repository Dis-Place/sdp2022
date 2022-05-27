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
import com.github.displace.sdp2022.database.FileStorage
import com.github.displace.sdp2022.database.FileStorageFactory
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

/**
 * Activity for editing the profile settings
 */
class AccountSettingsActivity : AppCompatActivity() {

    // Request codes when searching for images, used for the profile picture
    companion object {
        const val IMAGE_GALLERY_REQUEST = 300
        const val IMAGE_CAMERA_REQUEST = 400
    }

    private lateinit var username: TextView     // Username in a view in the activity
    private lateinit var profilePic: ImageView  // Profile picture in a view in the activity

    private var imageUri: Uri = Uri.EMPTY       // Uri to store the profile picture

    private lateinit var activeUser: CompleteUser   // Active user in the application

    // Reference for the profile picture in the database storage
    private lateinit var fileStorage: FileStorage

    /**
     * Launchers for the permissions
     */
    // Launcher for requesting the storage permission
    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted: Boolean ->
            if (accepted) {
                selectPicFromGallery()  // If permission is granted, we can select a picture from the gallery
            } else {
                showToastText("Please enable Storage permissions")
            }
        }

    // Launcher for requesting the camera permissions
    private val cameraPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            for ((_, v) in permissions.entries) {
                granted = granted && v
            }
            if (granted) {
                selectPicFromCamera()   // If permissions are granted, we can select a picture from the camera
            } else {
                showToastText("Please enable Camera & Storage permissions")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        username = findViewById(R.id.username)
        profilePic = findViewById(R.id.profilePic)

        profilePic.setTag(R.id.profilePic, "defaultPicTag") // Sets a tag on the default picture for testing


        val app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!

        // Reference to the profile pic in the database storage
        fileStorage = FileStorageFactory.getFileStorage(intent,"images/profilePictures/${activeUser.getPartialUser().uid}")

        if(activeUser.getPartialUser() != PartialUser("defaultName", "dummy_id")) { // That case is only when testing, and we don't search the image from the DB, should change with mock image DB
            if(activeUser.getProfilePic() == null) {    // Prevents the app from searching the image from the database everytime
                getProfilePicFromDatabase()
            } else {
                profilePic.setImageBitmap(activeUser.getProfilePic())   // Gets the saved profile picture
            }
        }

        username.text = activeUser.getPartialUser().username    // Sets the username

        app.getMessageHandler().checkForNewMessages()   // Checks any new message to receive notifications if so
    }

    /**
     * Gets the profile picture from the database
     */
    fun getProfilePicFromDatabase() {
        // Temporary local file for the profile pic
        val localFile = File.createTempFile("profilePic", "jpg")

        // Gets profile pic from database
        ProgressDialogsUtil.showProgressDialog(this)    // Shows progress dialog to prevent the user from uploading twice

        fileStorage.getThenCall(localFile,
            onSuccess = {
                val pic = BitmapFactory.decodeFile(localFile.absolutePath)
                activeUser.setProfilePic(pic)
                profilePic.setImageBitmap(pic)
                ProgressDialogsUtil.dismissProgressDialog()
            },
            onFailure = {
                showToastText("Failed to load profile pic")
                ProgressDialogsUtil.dismissProgressDialog()
            })
    }

    /**
     * Selects a picture for the profile picture, by asking the user for its provenance
     */
    @Suppress("UNUSED_PARAMETER")
    fun selectPic(view : View) {
        val options = arrayOf("Camera", "Gallery")

        val dialogBuilder = AlertDialog.Builder(this)   // Dialog to ask the user if the picture is from its camera or its gallery
        dialogBuilder.setTitle("Pick Image From")
        dialogBuilder.setItems(options) { _, pos ->
            if (pos == 0) {         // "Camera" button
                if (cameraPermissions()) {
                    selectPicFromCamera()
                } else {
                    requestCameraPermissions()
                }

            } else if (pos == 1) {  // "Storage" button
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

    /**
     * Request the camera permissions
     */
    private fun requestCameraPermissions() {
        cameraPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,                 // We need the permission for the camera and for saving the image
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    /**
     * Sends the intent to take a picture with the camera
     */
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

    /**
     * Request the necessary permissions for the gallery
     */
    private fun requestStoragePermissions() {
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)    // We need to have access to the storage
    }

    /**
     * Sends the intent to make the user choose a picture from its gallery
     */
    @Suppress("DEPRECATION")
    private fun selectPicFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST)
    }

    /**
     * Function called when the user chose a picture from its camera/gallery
     * @param requestCode: Code that shows the type of the request
     * @param resultCode: Code that gives information on how the activity executed
     * @param data: Data given by the activity, in our case the profile picture
     */
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            profilePic.setTag(R.id.profilePic, "modifiedTag")   // Changes the tag so that the automatic tests know that the picture changed
            if (requestCode == IMAGE_CAMERA_REQUEST) {      // If the picture comes from the camera
                uploadProfilePhoto(imageUri)               // imageUri will contain the picture (see selectPicFromCamera() )
            } else if (requestCode == IMAGE_GALLERY_REQUEST) {  // If the picture comes from the gallery
                uploadProfilePhoto(data?.data!!)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Uploads a profile picture on the database
     * @param imageUri: Uri of the picture we want to upload
     */
    private fun uploadProfilePhoto(imageUri: Uri?) {

        if (imageUri == null) {
            showToastText("Unable to upload profile photo")
        } else {
            fileStorage.put(imageUri,
                onSuccess = {
                    profilePic.setImageURI(imageUri)
                    showToastText("Profile pic uploaded")
                },
                onFailure = {
                    showToastText("Fail to upload profile pic")
                })
        }
    }

    /**
     * Prompts the user with a dialog that lets him change his username
     */
    @Suppress("UNUSED_PARAMETER")
    fun changeUsername(view: View) {
        // Creates the view for the username update dialog
        val newView = LayoutInflater.from(this).inflate(R.layout.username_update_dialog, null)

        // Elements of the dialog view
        val newName = newView.findViewById<EditText>(R.id.updateUsername)
        val newNameButton = newView.findViewById<Button>(R.id.updateUsernameButton)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(newView)
        val dialogUsername = dialogBuilder.create()
        dialogUsername.show()

        newNameButton.setOnClickListener {
            val name = newName.text.toString().trim()

            dialogUsername.dismiss()

            if (TextUtils.isEmpty(name)) {  // Check that the new username isn't empty
                showToastText("New Username can't be empty")
            } else {
                activeUser.changeUsername(name)     // Changing the username in the user will change it in the database
                username.text = name
            }
        }
    }

    /**
     * Helper method to show a toast Text on screen
     * @param string: Message to show
     */
    private fun showToastText(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
}