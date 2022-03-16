package com.github.blecoeur.bootcamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun settingsButton(view: View) {
        val intent = Intent(this, AccountSettingsActivity::class.java)
        startActivity(intent)
    }
}