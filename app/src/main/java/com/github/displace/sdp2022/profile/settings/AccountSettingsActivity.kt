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
    //private var processingAlert: AlertDialog? = null

    private val storageReference = Firebase.storage.reference

    private lateinit var activeUser: CompleteUser

    private lateinit var imgDBReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        //val passwordUpdate = findViewById<TextView>(R.id.passwordUpdate)

        username = findViewById(R.id.username)
        profilePic = findViewById(R.id.profilePic)

        profilePic.setTag(R.id.profilePic, "defaultPicTag") // For testing


        val app = applicationContext as MyApplication
        activeUser = app.getActiveUser()!!

        // Reference to the profile pic in Firebase Storage
        imgDBReference = storageReference.child("images/profilePictures/${activeUser.getPartialUser().uid}")

        // Temp file for the profile pic
        val localFile = File.createTempFile("profilePic", "jpg")

        // Gets profile pic from database
        imgDBReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profilePic.setImageBitmap(bitmap)
        }.addOnFailureListener{
            showToastText("Failed to load profile pic")
        }

        username.text = activeUser.getPartialUser().username

        /*passwordUpdate.setOnClickListener {
            showChangePasswordDialog()
        }*/
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

    // Old code for a change password that may be reused

    /*private fun showChangePasswordDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.password_update_dialog, null)
        val oldPassword = view.findViewById<EditText>(R.id.oldPasswordLog)
        val newPassword = view.findViewById<EditText>(R.id.newPasswordLog)
        val editPassword = view.findViewById<Button>(R.id.passwordUpdateButton)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(view)
        val dialogPassword = dialogBuilder.create()
        dialogPassword.show()

        editPassword.setOnClickListener {
            val oldP = oldPassword.text.toString().trim()
            val newP = newPassword.text.toString().trim()

            dialogPassword.dismiss()
            when {
                checkIfEmpty(oldP) -> {
                    showToastText("Current Password can't be empty")
                    return@setOnClickListener
                }
                checkIfEmpty(newP) -> {
                    showToastText("New Password can't be empty")
                    return@setOnClickListener
                }
            }
            dialogPassword.dismiss()
            updatePassword(oldP, newP)
        }
    }*/

    // Old code for a change password that may be reused

    /*private fun updatePassword(oldP: String, newP: String) {
        val authCredential: AuthCredential? =
            firebaseUser?.email?.let { EmailAuthProvider.getCredential(it, oldP) }

        if (authCredential != null) {
            firebaseUser?.reauthenticate(authCredential)?.addOnSuccessListener {
                actualPassword.text = newP
                if (firebaseUser != null) {
                    firebaseUser!!.updatePassword(newP).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToastText("Password changed")
                        } else {
                            showToastText("Password change failed")
                        }
                    }
                }
            }?.addOnFailureListener {
                showToastText("Incorrect password")
            }
        } else {
            if (oldP != actualPassword.text) {
                showToastText("Incorrect password")
            } else {
                actualPassword.text = newP
            }
        }
    }*/

    @Suppress("UNUSED_PARAMETER")
    private fun changeUsername(view: View) {
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

    // Unused code for a progress dialog that may be useful later
    /* fun setProgressDialog(progressMessage: String) {
         // Creating a Linear Layout
         val llPadding = 30
         val ll = LinearLayout(this)
         ll.orientation = LinearLayout.HORIZONTAL
         ll.setPadding(llPadding, llPadding, llPadding, llPadding)
         ll.gravity = Gravity.CENTER
         var llParam = LinearLayout.LayoutParams(
             LinearLayout.LayoutParams.WRAP_CONTENT,
             LinearLayout.LayoutParams.WRAP_CONTENT
         )
         llParam.gravity = Gravity.CENTER
         ll.layoutParams = llParam
         // Creating a ProgressBar inside the layout
         val progressBar = ProgressBar(this)
         progressBar.isIndeterminate = true
         progressBar.setPadding(0, 0, llPadding, 0)
         progressBar.layoutParams = llParam
         llParam = LinearLayout.LayoutParams(
             ViewGroup.LayoutParams.WRAP_CONTENT,
             ViewGroup.LayoutParams.WRAP_CONTENT
         )
         llParam.gravity = Gravity.CENTER
         // Creating a TextView inside the layout
         val tvText = TextView(this)
         tvText.text = progressMessage
         tvText.setTextColor(Color.BLACK)
         tvText.textSize = 20f
         tvText.layoutParams = llParam
         ll.addView(progressBar)
         ll.addView(tvText)
         // Setting the AlertDialog Builder view
         // as the Linear layout created above
         val builder: AlertDialog.Builder = AlertDialog.Builder(this)
         builder.setCancelable(true)
         builder.setView(ll)
         // Displaying the dialog
         processingAlert = builder.create()
         processingAlert?.show()
         val window: Window? = processingAlert?.window
         if (window != null) {
             val layoutParams = WindowManager.LayoutParams()
             layoutParams.copyFrom(processingAlert?.window?.attributes)
             layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
             layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
             processingAlert?.window?.attributes = layoutParams
             // Disabling screen touch to avoid exiting the Dialog
             window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
         }
     }*/

    private fun checkIfEmpty(string: String): Boolean {
        return TextUtils.isEmpty(string)
    }

    private fun showToastText(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
}