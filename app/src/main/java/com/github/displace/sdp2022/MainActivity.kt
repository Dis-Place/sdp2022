package com.github.displace.sdp2022

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.OfflineUserFetcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.mainActivityLogInButton)
        val isRemembered =
            getSharedPreferences("login", MODE_PRIVATE).getBoolean("remembered", false)

        //isConnected is true if the user is connected to the internet

        loginButton.setOnClickListener {
            //Will chose how we log in, depending on whether we are already remembered and we are connected to the internet
            val app = applicationContext as MyApplication

            //TODO:Find a way to get the firebase user
            if (isRemembered) {
                if (isConnected()) {
                    //As we are remembered and we are online, we can have a normal logged in profiles
                    if (app.getActiveUser() == null) {
                        val user = CompleteUser(this, null, offlineMode = true)
                        app.setActiveUser(user)
                    }
                } else {
                    val user = OfflineUserFetcher(this).getCompleteUser()
                    app.setActiveUser(user)
                }
                Intent(this, MainMenuActivity::class.java).apply {
                    startActivity(this)
                }
            } else if (isConnected()) {
                Intent(this, SignInActivity::class.java).apply {
                    startActivity(this)
                }
            } else {
                makeText(
                    this,
                    "You need an internet connection to log in when you are not remembered in !",
                    LENGTH_SHORT
                ).show()
            }

        }
    }

    /**
     * Just a utility function to check if the user is connected to the internet or not
     *
     * @return
     */
    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}