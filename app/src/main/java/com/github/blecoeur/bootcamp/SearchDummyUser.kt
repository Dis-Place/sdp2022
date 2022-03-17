package com.github.blecoeur.bootcamp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class SearchDummyUser : AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_dummy_user)

        db =
            FirebaseDatabase.getInstance("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    fun searchAge(view: View) {
        val ageText = findViewById<TextView>(R.id.dummySearchAgeTextView)
        val nameText = findViewById<EditText>(R.id.dummySearchName)

        db.getReference("users").child(nameText.text.toString()).get().addOnSuccessListener {
            if (it.exists())
                ageText.apply { text = it.child("age").value.toString() + " years old" }
            else
                ageText.apply { text = "Sorry, this user does not exist" }
        }.addOnFailureListener {
            ageText.apply { text = "Sorry, the database request failed" }
        }
    }

}