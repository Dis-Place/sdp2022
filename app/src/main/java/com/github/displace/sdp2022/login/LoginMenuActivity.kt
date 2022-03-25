package com.github.displace.sdp2022.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.R

/*
 * A simple UI class that allows to launch activities in order to login
 * @author blecoeur
 */
class LoginMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)
    }

    /*
     * Function called when the "Using a google account" button is pressed
     */
    @Suppress("UNUSED_PARAMETER")
    fun launchGoogleLoginActivity(view: View) {
        //TODO:Replace the MainMenuActivity by the needed activity (and do the same in the tests)
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    /*
     * Function called when the "Without google" button is pressed
     */
    @Suppress("UNUSED_PARAMETER")
    fun launchNormalLoginActivity(view: View) {
        //TODO:Replace the MainMenuActivity by the needed activity (and do the same in the tests)
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
}