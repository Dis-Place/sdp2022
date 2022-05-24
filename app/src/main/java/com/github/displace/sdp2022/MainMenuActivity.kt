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


    /**
     * When the activity is created.
     * Updates the UI and sets up the messaging system.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)

        updateUI()

        val app = applicationContext as MyApplication
        val handler = MessageHandler(app.getActiveUser()!!.getPartialUser(), app,intent)
        app.setMessageHandler(handler)
        app.getMessageHandler().checkForNewMessages()
    }

    /**
     * When the activity is resumed.
     * Adds a new listener for the users' messages and update the UI (as the name could have changed)
     */
    override fun onResume() {
        super.onResume()
        updateUI()
        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()

    }

    /**
     * When the activity is destroyed.
     * Removes the listener for the users' messages.
     * TODO : check if removing the user from the DB should be done here
     */
    override fun onDestroy() {
        val app = applicationContext as MyApplication
        val user = app.getActiveUser()!!
        if (user.guestBoolean) {
            user.removeUserFromDatabase()
        }
        super.onDestroy()
    }



    /**
     * Updates the User Interface with the correct username.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        //load the username from the application
        val app = applicationContext as MyApplication
        val activeUser = app.getActiveUser()
        val message = activeUser?.getPartialUser()?.username ?: "defaultNotLoggedIn"

        findViewById<TextView>(R.id.WelcomeText).apply {
            text =
                "Welcome $message!"
        }
    }

    /**
     * Function used by a button on the view.
     * Sends the user to the Game List, which allows to choose a game mode.
     *
     * @param view : the view of the activity, will not be used
     */
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
    /**
     * Function used by a button on the view.
     * Sends the user to the Login Menu and notifies that the user has to be logged out.
     *
     * @param view : the view of the activity, will not be used
     */
    @Suppress("UNUSED_PARAMETER")
    fun signOut(view: View) {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
            getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", false).apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Function used by a button on the view.
     * Sends the user to the Profile, which allows to view and edit the users' profile.
     *
     * @param view : the view of the activity, will not be used
     */
    @Suppress("UNUSED_PARAMETER")
    fun profileButton(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    /**
     * Function used by a button on the view.
     * Sends the user to the Settings, which allows to change the applications general settings.
     *
     * @param view : the view of the activity, will not be used
     */
    @Suppress("UNUSED_PARAMETER")
    fun settingsButton(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    /**
     * Function used by a button on the view.
     * Sends the user to the News, which allows to view applications news and updates.
     *
     * @param view : the view of the activity, will not be used
     */
    @Suppress("UNUSED_PARAMETER")
    fun newsButton(view: View) {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    //TODO() : DELETE
    @Suppress("UNUSED_PARAMETER")
    fun openMap(view: View) {
        val intent =
            Intent(this, DemoMapActivity::class.java).apply { }
        startActivity(intent)
    }




}