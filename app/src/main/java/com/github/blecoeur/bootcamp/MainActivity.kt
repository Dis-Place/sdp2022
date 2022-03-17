package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.blecoeur.bootcamp.profile.MockDB
import com.github.blecoeur.bootcamp.profile.friends.Friend


class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    private val myPreferences = "myPrefs"
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAppUse(view: View) {

        //set application data : start DB connection
        val app = applicationContext as MyApplication
        app.setDb(MockDB())

        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()
        //load the username in the preferences for later use
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        app.setActiveUser(Friend(name, "0"))

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }

}