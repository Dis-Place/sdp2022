package com.github.displace.sdp2022

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.displace.sdp2022.authentication.TempLoginActivity
import com.github.displace.sdp2022.profile.MockDB


class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    private val myPreferences = "myPrefs"
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = applicationContext as MyApplication
        app.setDb(MockDB())

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        val intent =
            Intent(this, TempLoginActivity::class.java).apply { }
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun startAppUse(view: View) {

        //set application data : start DB connection
        val app = applicationContext as MyApplication
        app.setDb(MockDB())

        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()
        //load the username in the preferences for later use
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        //Load the default settings values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun toLogin(view: View) {
        val intent =
            Intent(this, TempLoginActivity::class.java).apply { }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        startActivity(Intent(this, TempLoginActivity::class.java))
    }


}