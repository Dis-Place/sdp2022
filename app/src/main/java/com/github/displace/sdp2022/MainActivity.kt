package com.github.displace.sdp2022

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.profile.MockDB
import com.github.displace.sdp2022.profile.friends.Friend
import com.github.displace.sdp2022.R


class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    private val myPreferences = "myPrefs"
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Suppress("UNUSED_PARAMETER")
    fun startAppUse(view: View) {

        //set application data : start DB connection
        val app = applicationContext as MyApplication
        app.setDb(MockDB())
        app.setDbRt(RealTimeDatabase())
        app.setDbNonCache(RealTimeDatabase())

        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()
        //load the username in the preferences for later use
        sharedpreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        app.setActiveUser(Friend(name, "0"))
        app.getDb().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false)
        app.getDbNonCache().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false)

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }

}