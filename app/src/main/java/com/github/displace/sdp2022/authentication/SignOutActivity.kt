package com.github.displace.sdp2022.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        val signOutButton = findViewById<Button>(R.id.signOutActivitySignOutButton)

        signOutButton.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                    (applicationContext as MyApplication).getMessageHandler().removeListener()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Cannot log out you're not logged in", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
}