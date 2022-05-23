package com.github.displace.sdp2022

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.database.CleanUpGuests
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.util.CheckConnectionUtil.checkForInternet
import kotlin.random.Random

/**
 * Main Activity launched when the application starts
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Random.nextInt(0, 10) == 0) {
            CleanUpGuests.cleanUpDatabaseFromGuests(intent)         // Removes all guest users from the database
        // We only do this every 10 times, because it requires to go through
        // all the users in the database and it wouldn't be efficient to do it every time
        }

        val enterAppButton = findViewById<Button>(R.id.mainActivityLogInButton)

        val isRemembered =
            getSharedPreferences("login", MODE_PRIVATE).getBoolean("remembered", false) // Get if an user is remembered

        enterAppButton.setOnClickListener { enterApp(isRemembered) }
    }

    /**
     * Sends the user to the sign in activity, or enters the applications if a user is remembered
     * @param isRemembered: If an user is remembered or not
     */
    private fun enterApp(isRemembered: Boolean) {
        val app = applicationContext as MyApplication

        if (isRemembered) {
            val user = CompleteUser(app,
                null,
                offlineMode = !checkForInternet(this), // we check if we're offline
                remembered = true   // We are remembered
            )
            app.setActiveUser(user)
            Intent(this, MainMenuActivity::class.java).apply {
                startActivity(this)          // We can directly enter the app without going through the sign in since the user is remembered
            }
        } else if (checkForInternet(this)) {
            Intent(this, SignInActivity::class.java).apply {
                startActivity(this)         // If the user is not remembered but online, we have to go through the sign in
            }
        } else {  // If the user is not remembered and there is no internet connection
            makeText(
                this,
                "You need an internet connection to log in when you are not remembered in !",
                LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Called when the Main Activity is closed (but not systematically)
     */
    override fun onDestroy() {
        val activeUser = (applicationContext as MyApplication).getActiveUser()
        if(activeUser != null && activeUser.guestBoolean) {         // If we leave the Main Activity, we want to erase our now useless guest user
            activeUser.removeUserFromDatabase()
        }
        super.onDestroy()
    }
}