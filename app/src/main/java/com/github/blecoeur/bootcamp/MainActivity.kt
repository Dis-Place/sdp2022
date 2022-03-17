package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    val myPreferences = "myPrefs"
    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAppUse(view: View) {
        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()
        //load the username in the preferences for later use
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("userNameKey",name)
        editor.commit()

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }

}