package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainMenuActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    val myPreferences = "myPrefs"
    lateinit var sharedpreferences: SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        //load the username from the preferences
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var message = sharedpreferences.getString("userNameKey", "NOT FOUND")
        val welcomeTextView =  findViewById<TextView>(R.id.WelcomeText).apply { text =
            "Welcome $message!"
        }

    }


    //send the user to the Play screen : start a match
    fun playButton(view: View) {
        val intent = Intent(this, PlayActivity::class.java)
        startActivity(intent)
    }

    //send the user to the Profile screen : view stats + edit profile
    fun profileButton(view: View){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    //send the user to the Settings screen : change the apps settings
    fun settingsButton(view: View){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    //send the user to the News screen : view news and updates
    fun newsButton(view: View){
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    //send the user to the database demonstration
    fun databaseDemoButton(view: View){
        //val intent = Intent(this, DummyLoginActivity::class.java)
        //startActivity(intent)
    }


}