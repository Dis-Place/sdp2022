package com.github.displace.sdp2022.authentication

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.CompleteUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val REQUEST_CODE_SIGN_IN = 0

class SignInActivity : AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var rememberMeButton: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = Firebase.auth

        val signInButton = findViewById<Button>(R.id.signInActivitySignInButton)
        val guestModeButton = findViewById<Button>(R.id.signInActivityGuestModeButton)
        rememberMeButton = findViewById(R.id.signInActivityRememberMeCheckBox)

        rememberMeButton.isChecked = true

        // Signing in means logging in thanks to the Google sign in platform,
        // and checking whether we want to be remembered or not so that we adapt our results
        signInButton.setOnClickListener {

            val sharedPreferences =  getSharedPreferences("login", MODE_PRIVATE)

            if(rememberMeButton.isChecked) {
                sharedPreferences.edit().putBoolean("remembered", true).apply()
            }

            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id)).requestEmail().build()
            signInClient = GoogleSignIn.getClient(this, options)
            startActivityForResult(signInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }

            //Signing in as a guest means that we have to create a new firebase user profile
        guestModeButton.setOnClickListener {
            signInAsGuest(it)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val app = applicationContext as MyApplication
        app.setActiveUser(null)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                }
            } catch (e: Exception) {
                Log.e("debug", e.message!!)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun signInAsGuest(view: View) {

        val app = applicationContext as MyApplication
        app.setActiveUser(null)

        auth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                handleNewUser(true)
                
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun handleNewUser(isGuest: Boolean = false): Boolean {
        val app = applicationContext as MyApplication
        val current = auth.currentUser

        val name: String? = if (isGuest) "guest" else current?.displayName
        return if (name.isNullOrEmpty()) {
            showFailedSignInMessage()
            false
        } else {
            Toast.makeText(
                this@SignInActivity,
                "Successfully logged in $name ",
                Toast.LENGTH_LONG
            ).show()
            val user = CompleteUser(app, current, guestBoolean = isGuest)
            app.setActiveUser(user)
            true
        }
    }

    private fun showFailedSignInMessage() {
        Toast.makeText(
            this@SignInActivity,
            "Failed to authenticate",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {

                    val app = applicationContext as MyApplication
                    val current = auth.currentUser

                    val name: String? = current?.displayName
                    if (name.isNullOrEmpty()) {
                        Toast.makeText(
                            this@SignInActivity,
                            "Failed to authenticate",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Successfully logged in $name ",
                            Toast.LENGTH_LONG
                        ).show()

                        val user =
                            CompleteUser(app, current)
                        app.setActiveUser(user)
                        delay(1_000)
                        startActivity(Intent(this@SignInActivity, MainMenuActivity::class.java))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}