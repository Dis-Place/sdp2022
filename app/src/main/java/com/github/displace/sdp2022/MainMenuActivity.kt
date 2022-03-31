package com.github.displace.sdp2022

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.news.NewsActivity
import com.github.displace.sdp2022.profile.ProfileActivity


class MainMenuActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        //load the username from the application
        val app = applicationContext as MyApplication
        val message = app.getActiveUser().getPartialUser().username
        findViewById<TextView>(R.id.WelcomeText).apply {
            text =
                "Welcome $message!"
        }

    }


    //send the user to the Play screen : start a match
    @Suppress("UNUSED_PARAMETER")
    fun playButton(view: View) {
        val intent = Intent(this, GameListActivity::class.java)
        startActivity(intent)
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

    //send the user to the database demonstration
    @Suppress("UNUSED_PARAMETER")
    fun databaseDemoButton(view: View) {
        val intent = Intent(this, UploadImageActivity::class.java)
        startActivity(intent)
    }


}