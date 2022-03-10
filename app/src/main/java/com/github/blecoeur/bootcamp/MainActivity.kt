package com.github.blecoeur.bootcamp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase

<<<<<<< HEAD:app/src/main/java/com/github/blecoeur/bootcamp/MainActivity.kt
=======
const val EXTRA_MESSAGE = "com.github.displace.sdp2022.MESSAGE"
private lateinit var analytics: FirebaseAnalytics
private lateinit var db: FirebaseDatabase

>>>>>>> database_setup:app/src/main/java/com/github/displace/sdp2022/MainActivity.kt
class MainActivity : AppCompatActivity() {

    //preferences setup : using a dummy name
    val myPreferences = "myPrefs"
    lateinit var sharedpreferences: SharedPreferences;

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

        val intent =
<<<<<<< HEAD:app/src/main/java/com/github/blecoeur/bootcamp/MainActivity.kt
            Intent(this, MainMenuActivity::class.java)
=======
            Intent(this, DummyLoginActivity::class.java).apply { putExtra(EXTRA_MESSAGE, name) }
>>>>>>> database_setup:app/src/main/java/com/github/displace/sdp2022/MainActivity.kt
        startActivity(intent)
    }

}