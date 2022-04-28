package com.github.displace.sdp2022.authentication

import android.content.Context
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
import com.github.displace.sdp2022.users.OfflineUserFetcher
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

class TempLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: GoogleSignInClient
    private val LOGIN_REFERENCE = "login-checkbox"
    private val REMEMBER_REFERENCE = "login-remember"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_login)
        auth = Firebase.auth

        val sharedPreferences = getSharedPreferences(LOGIN_REFERENCE, MODE_PRIVATE)


        val signInButton = findViewById<Button>(R.id.btnGoogleSignIn)
        signInButton.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id)).requestEmail().build()
            signInClient = GoogleSignIn.getClient(this, options)

            startActivityForResult(signInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }
        val logout = findViewById<Button>(R.id.btnGoogleSignOut)
        logout.setOnClickListener {

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


        val offlineLogin = findViewById<Button>(R.id.goToAppOfflineButton)
        offlineLogin.setOnClickListener {
            if (sharedPreferences.getBoolean("login-checkbox", false)) {
                val app = applicationContext as MyApplication
                val user = CompleteUser(this, null, offlineMode = true)
                app.setActiveUser(user)
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cannot use offline mode, no user were cached before", Toast.LENGTH_LONG).show()
            }
        }
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
                            this@TempLoginActivity,
                            "Failed to authenticate",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@TempLoginActivity,
                            "Successfully logged in $name ",
                            Toast.LENGTH_LONG
                        ).show()
                        val user = CompleteUser(this@TempLoginActivity, current)
                        app.setActiveUser(user)
                        val remember = findViewById<CheckBox>(R.id.loginRememberCheckBox)
                        if (remember.isChecked) {
                            val offlineUserFetcher = OfflineUserFetcher(this@TempLoginActivity)
                            offlineUserFetcher.setCompleteUser(user)
                            val sharedPreferences =
                                getSharedPreferences("login-checkbox", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("login-remember", true).apply()
                        }
                    }
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
        val offlineButton = findViewById<Button>(R.id.goToAppOfflineButton)

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
    fun startOffline(view: View) {
        val app = applicationContext as MyApplication
        val user = CompleteUser(this, null, guestBoolean = true, readOnly = true)
        app.setActiveUser(user)
        goToMainMenuActivity(view)
    }


    @Suppress("UNUSED_PARAMETER")
    fun enterMenu(view: View) {
        goToMainMenuActivity(view)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun goToMainMenuActivity(view: View) {
        startActivity(Intent(this@TempLoginActivity, MainMenuActivity::class.java))
    }

}
