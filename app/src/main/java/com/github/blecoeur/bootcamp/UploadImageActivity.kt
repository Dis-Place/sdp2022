package com.github.blecoeur.bootcamp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class UploadImageActivity : AppCompatActivity() {
    private lateinit var db: ImageDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        db = ImageDatabase().instantiate("gs://displace-dd51e.appspot.com/") as ImageDatabase
    }

    fun imageUpload(view: View) {
        val imageView = findViewById<ImageView>(R.id.imageUpload)
        db.insert(String(), imageView.drawable.toString(), imageView)
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }


    fun openGallery(view: View) {
        Log.i("debug", "trying to open the gallery")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }
}