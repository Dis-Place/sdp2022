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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase

const val EXTRA_MESSAGE = "com.github.displace.sdp2022.MESSAGE"
private lateinit var analytics: FirebaseAnalytics
private lateinit var db: FirebaseDatabase

class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    val myPreferences = "myPrefs"
    lateinit var sharedpreferences: SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAppUse(view: View) {

        //set application data : start DB connection
        val app = applicationContext as MyApplication
        app.setDb(MockDB())
        app.getProfileDb().setDbId(4561)

        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()
        //load the username in the preferences for later use
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        app.setActiveUser( Friend(name,"0") )
/*
        val editor = sharedpreferences.edit()
        editor.putString("userNameKey",name)
        editor.commit()
*/
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }
}