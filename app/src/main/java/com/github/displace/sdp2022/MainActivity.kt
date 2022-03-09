package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase

const val EXTRA_MESSAGE = "com.github.displace.sdp2022.MESSAGE"
private lateinit var analytics: FirebaseAnalytics
private lateinit var db: FirebaseDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sendMessage(view: View) {
        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()

        val intent =
            Intent(this, DummyLoginActivity::class.java).apply { putExtra(EXTRA_MESSAGE, name) }
        startActivity(intent)
    }
}