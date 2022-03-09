package com.github.blecoeur.bootcamp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class AccountSettingsActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_GALLERY_REQUEST = 300
        const val IMAGE_CAMERA_REQUEST = 400
    }

    /*var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null*/

    private val storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            accepted: Boolean ->
        if(accepted) {
            selectPicFromGallery()
        } else {
            Toast.makeText(this, "Please enable Storage permissions", Toast.LENGTH_LONG).show()
        }
    }

    private val cameraPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions ->
        var granted = true
        for((_,v) in permissions.entries) {
            granted = granted && v
        }
        if(granted) {
            selectPicFromCamera()
        } else {
            Toast.makeText(this, "Please enable Camera & Storage permissions", Toast.LENGTH_LONG).show()
        }
    }

    private var processingAlert: AlertDialog? = null
    private var profilePic: ImageView? = null

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)


        val profilePicUpdate = findViewById<TextView>(R.id.profilePicUpdate)
        val usernameUpdate = findViewById<TextView>(R.id.usernameUpdate)
        val passwordUpdate = findViewById<TextView>(R.id.passwordUpdate)

        profilePic = findViewById(R.id.profilePic)

        /*firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser*/


        profilePicUpdate.setOnClickListener {
            selectPic()
        }

        passwordUpdate.setOnClickListener {
            showChangePasswordDialog()
        }

        usernameUpdate.setOnClickListener{
            changeUsername()
        }
    }

    /*fun setProgressDialog(progressMessage: String) {
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

    private fun selectPic() {
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
                if(storagePermissions()) {
                    selectPicFromGallery()
                } else {
                    requestStoragePermissions()
                }
            }
        }
        dialogBuilder.create().show()
    }

    private fun cameraPermissions(): Boolean {

        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)

    }

    private fun requestCameraPermissions() {

        cameraPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            //ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST)

    }

    private fun selectPicFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAMERA_REQUEST)
        /*val startCamera = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri ->
            profilepic?.setImageURI(uri)
        }*/
        /*val startCamera = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> {
                result -> //onActivityResult(IMAGE_CAMERA_REQUEST, result)
                    if(result.resultCode == RESULT_OK) {
                        profilepic?.setImageURI(imageUri)
                    }
                /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    super.onActivityResult(requestCode, resultCode, data)
                }*/
            })*/
        //startCamera.launch(cameraIntent)
    }

    private fun storagePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == (PackageManager.PERMISSION_GRANTED))
    }

    private fun requestStoragePermissions() {
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST)
    }

    private fun selectPicFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_CAMERA_REQUEST) {
                profilePic?.setImageURI(imageUri)
            } else if(requestCode == IMAGE_GALLERY_REQUEST) {
                profilePic?.setImageURI(data?.data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if(result.resultCode == Activity.RESULT_OK) {
            if(requestCode == IMAGE_GALLERY_REQUEST) {
                imageUri = result.data?.data
                uploadProfilePhoto(imageUri)
            }
            if(requestCode == IMAGE_CAMERA_REQUEST) {
                val photo: Bitmap = result.data?.extras?.get("data") as Bitmap
                profilepic?.setImageBitmap(photo)
                //uploadProfilePhoto(imageUri)
            }
        }
    } */          // fix try for deprecation

    private fun uploadProfilePhoto(imageUri: Uri?) {
        TODO("Not yet implemented")
    }

    private fun showChangePasswordDialog() {
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
            if(TextUtils.isEmpty(oldP)) {
                Toast.makeText(this, "Current Password can't be empty", Toast.LENGTH_LONG).show()
            } else if(TextUtils.isEmpty(newP)) {
                Toast.makeText(this, "New Password can't be empty", Toast.LENGTH_LONG).show()
            } else {
                updatePassword(oldP, newP)
            }
        }
    }

    private fun updatePassword(oldP: String, newP: String) {

    }

    private fun changeUsername() {
        TODO("Not yet implemented")
    }



}