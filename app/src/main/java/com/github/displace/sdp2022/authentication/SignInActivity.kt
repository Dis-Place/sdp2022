package com.github.displace.sdp2022.authentication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.util.ProgressDialogsUtil
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

/**
 * Activity for signing in the application, with Google or as a guest
 */
class SignInActivity : AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var rememberMeButton: CheckBox
    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = Firebase.auth
        app = applicationContext as MyApplication

        val signInButton = findViewById<Button>(R.id.signInActivitySignInButton)
        val guestModeButton = findViewById<Button>(R.id.signInActivityGuestModeButton)
        rememberMeButton = findViewById(R.id.signInActivityRememberMeCheckBox)

        rememberMeButton.isChecked = true       // We check the Remember Me button by default since in most cases, an user will want the app to remember him

        // Signing in means logging in thanks to the Google sign in platform,
        // and checking whether we want to be remembered or not so that we adapt our results
        signInButton.setOnClickListener {
            ProgressDialogsUtil.showProgressDialog(this)    // Shows a progress dialog to prevent the user from touching any button during sign in

            // If SignInActivity is launched, it means that the app doesn't remember him, so we can launch the google sign
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id)).requestEmail().build()
            signInClient = GoogleSignIn.getClient(this, options)

            startActivityForResult(signInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }

        // Signing in as a guest
        guestModeButton.setOnClickListener {
            ProgressDialogsUtil.showProgressDialog(this)    // Shows a progress dialog to prevent the user from touching any button during sign in
            signInAsGuest(it)
        }

    }

    // Method called when the Google account is chosen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result       // Gets the account the user choosed to sign in with
                account?.let {
                    googleAuthForFirebase(it)
                }
            } catch (e: Exception) {
                ProgressDialogsUtil.dismissProgressDialog()     // Removes the progress dialog if there is one shown
                Log.e("debug", e.message!!)
            }
        }
    }

    /**
     * Sign in anonymously (for the guest mode)
     */
    @Suppress("UNUSED_PARAMETER")
    fun signInAsGuest(view: View) {
        auth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                handleNewUser(true)
            }
        }
    }

    /**
     * Tries to sign in to its Google account
     */
    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {     // sign in is asynchronous
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    // Store the remember me only if the sign in succeeded
                    val sharedPreferences =  getSharedPreferences("login", MODE_PRIVATE)

                    if(rememberMeButton.isChecked) {
                        sharedPreferences.edit().putBoolean("remembered", true).apply()     // Remember Be is stored in the shared preferences
                    }
                    handleNewUser(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ProgressDialogsUtil.dismissProgressDialog()     // Removes the progress dialog if there is one shown
                    Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Creates the user depending on if it is a guest
     * @param isGuest did the user sign in as a guest
     */
    private fun handleNewUser(isGuest: Boolean) {
        val currentUser = auth.currentUser

        val name: String? = if (isGuest) "guest" else currentUser?.displayName
        if (name.isNullOrEmpty()) {      // If there's no name, it means the current user is null, so the sign in failed
            ProgressDialogsUtil.dismissProgressDialog()     // Removes the progress dialog if there is one shown
            showFailedSignInMessage()
        } else {
            Toast.makeText(
                this@SignInActivity,
                "Successfully logged in as $name ",
                Toast.LENGTH_LONG
            ).show()

            // Set the user accordingly
            val user = CompleteUser(app, currentUser, guestBoolean = isGuest, activity = this@SignInActivity)      // needs SignInActivity to launch main menu asynchronously
            app.setActiveUser(user)
        }
    }


    private fun showFailedSignInMessage() {
        Toast.makeText(
            this@SignInActivity,
            "Failed to authenticate",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Launches the main menu
     * Called when the user is fully set up
     */
    fun launchMainMenuActivity() {
        ProgressDialogsUtil.dismissProgressDialog() // Removes the progress dialog if there is one shown
        startActivity(Intent(this@SignInActivity, MainMenuActivity::class.java))    // Launch the main menu
    }
}