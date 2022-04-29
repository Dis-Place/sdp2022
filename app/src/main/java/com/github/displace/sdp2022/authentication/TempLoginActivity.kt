package com.github.displace.sdp2022.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0
const val GUEST_EXTRA_ID = "GUEST_LOGIN"

class TempLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: GoogleSignInClient
    private lateinit var rememberMeButton: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_login)
        auth = Firebase.auth
        rememberMeButton = findViewById(R.id.loginRememberCheckBox)

        val sharedPreferences = getSharedPreferences("login-checkbox", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("login-checkbox", false)) {
            val app = applicationContext as MyApplication
            val user = CompleteUser(this,null, offlineMode = true)
            app.setActiveUser(user)

            Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please login", Toast.LENGTH_LONG).show()
        }

        //    //Check if internet is available, can be useful
        //    if (Connectivity.isConnected(applicationContext)) {

    }

    @Suppress("UNUSED_PARAMETER")
    fun launchSignIn(view: View) {
        /*if (sharedPreferences.getBoolean("login-remember", false)){
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
            }*/
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id)).requestEmail().build()
        signInClient = GoogleSignIn.getClient(this, options)

        startActivityForResult(signInClient.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    @Suppress("UNUSED_PARAMETER")
    fun launchSignOut(view: View) {
        if (Firebase.auth.currentUser != null) {
            AuthUI.getInstance().signOut(this@TempLoginActivity).addOnCompleteListener {
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                (applicationContext as MyApplication).getMessageHandler().removeListener()
            }

        } else {
            Toast.makeText(this, "Cannot log out you're not logged in", Toast.LENGTH_LONG)
                .show()
        }
        updateUI(false)
    }

    @Suppress("UNUSED_PARAMETER")
    fun signInAsGuest(view: View) {
        auth.signInAnonymously().addOnCompleteListener {
            if(it.isSuccessful) {
                updateUI(handleNewUser(true))
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    handleNewUser()
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TempLoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result

                account?.let {

                    googleAuthForFirebase(it)
                }
            } catch (e: Exception) {
                Log.e("test", e.message!!)
            }

        }

        updateUI(true)
    }

    private fun updateUI(loggingIn: Boolean) {
        val signInButton = findViewById<Button>(R.id.btnGoogleSignIn)
        val onlineButton = findViewById<Button>(R.id.goToAppOnlineButton)
        val offlineButton = findViewById<Button>(R.id.guestSignInButton)

        if (loggingIn) {
            signInButton.visibility = View.GONE
            onlineButton.visibility = View.VISIBLE
            offlineButton.visibility = View.GONE
        } else {
            signInButton.visibility = View.VISIBLE
            onlineButton.visibility = View.GONE
            offlineButton.visibility = View.VISIBLE
        }

    }

    @Suppress("UNUSED_PARAMETER")
    fun rememberMeInput(view: View) {
        if (rememberMeButton.isChecked) {
            /*val editor = sharedPreferences.edit()
            editor.putBoolean("login-remember", true)
            editor.apply()*/
            Toast.makeText(this, "Remember Me is checked", Toast.LENGTH_SHORT).show()
        } else {
            /*val editor = sharedPreferences.edit()
            editor.putBoolean("login-remember", false)
            editor.apply()*/
            Toast.makeText(this, "Remember Me is unchecked", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun enterMenu(view: View) {
        goToMainMenuActivity(view)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun goToMainMenuActivity(view: View) {
        startActivity(Intent(this@TempLoginActivity, MainMenuActivity::class.java))
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
                this@TempLoginActivity,
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
            this@TempLoginActivity,
            "Failed to authenticate",
            Toast.LENGTH_LONG
        ).show()
    }

}
