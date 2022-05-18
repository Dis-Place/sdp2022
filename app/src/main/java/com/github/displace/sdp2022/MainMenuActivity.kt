package com.github.displace.sdp2022

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.github.displace.sdp2022.news.NewsActivity
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.messages.MessageHandler


class MainMenuActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)

        updateUI()

        val app = applicationContext as MyApplication
        val handler = MessageHandler(app.getActiveUser()!!.getPartialUser(), app)
        app.setMessageHandler(handler)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }


    override fun onDestroy() {
        val app = applicationContext as MyApplication
        val user = app.getActiveUser()!!
        if (user.guestBoolean) {
            user.removeUserFromDatabase()
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()

        val app = applicationContext as MyApplication
        app.getMessageHandler().removeListener()
    }


    //send the user to the Play screen : start a match
    @Suppress("UNUSED_PARAMETER")
    fun playButton(view: View) {
        val app = applicationContext as MyApplication
        val user = app.getActiveUser()
        if (user == null || user.offlineMode) {
            Toast.makeText(this, "You're offline ! It's still an online game...", Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(this, GameListActivity::class.java)
            startActivity(intent)
        }

    }

    @Suppress("UNUSED_PARAMETER")
    fun signOut(view: View) {
        val app = applicationContext as MyApplication
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
            app.getMessageHandler().removeListener()
            getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", false).apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if(app.getActiveUser() != null) {
            if (app.getActiveUser()!!.guestBoolean) {
                app.getActiveUser()!!.removeUserFromDatabase()
            }
        }
    }

    //send the user to the Profile screen : view stats + edit profile
    @Suppress("UNUSED_PARAMETER")
    fun profileButton(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    //send the user to the Settings screen : change the apps settings
    @Suppress("UNUSED_PARAMETER")
    fun settingsButton(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    //send the user to the News screen : view news and updates
    @Suppress("UNUSED_PARAMETER")
    fun newsButton(view: View) {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun updateUI() {
        //load the username from the application
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        val message = if (activeUser == null) {
            "defaultNotLoggedIn"
        } else {
            activeUser.getPartialUser().username
        }
        findViewById<TextView>(R.id.WelcomeText).apply {
            text =
                "Welcome $message!"
        }
    }


}