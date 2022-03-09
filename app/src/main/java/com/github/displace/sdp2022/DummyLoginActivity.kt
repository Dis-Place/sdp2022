package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

const val EXTRA_USER = "com.github.displace.sdp2022.USER"

class DummyLoginActivity : AppCompatActivity() {
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_login)
        db =
            FirebaseDatabase.getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    fun sendUser(view: View) {
        val nameText = findViewById<EditText>(R.id.dummyName)
        val ageText = findViewById<EditText>(R.id.dummyAge)

        val name = nameText.text.toString()
        var age: Int
        try {
            age = ageText.text.toString().toInt()
        } catch (exception: NumberFormatException) {
            age = 0
        }

        val user = DummyUser(name, age)

        db.getReference("users").child(nameText.text.toString()).setValue(user)

        val intent = Intent(this, SearchDummyUser::class.java).apply {
            putExtra(EXTRA_USER, user)
        }
        startActivity(intent)
    }
}