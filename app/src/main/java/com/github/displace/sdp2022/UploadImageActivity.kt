package com.github.displace.sdp2022

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class UploadImageActivity : AppCompatActivity() {
    private lateinit var db: ImageDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        db = ImageDatabase().instantiate("gs://displace-dd51e.appspot.com/",false) as ImageDatabase
    }

    @Suppress("UNUSED_PARAMETER")
    fun imageUpload(view: View) {
        val imageView = findViewById<ImageView>(R.id.imageUpload)
        try {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            db.insert(String(), imageView.drawable.toString(), bitmap)
        } catch (e: IOException) {
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
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }


    @Suppress("UNUSED_PARAMETER")
    fun openGallery(view: View) {
        Log.i("debug", "trying to open the gallery")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivity(intent)
    }
}