package com.github.displace.sdp2022.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class TempLoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var signInClient :GoogleSignInClient
    private var isLoggedIn = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_login)
        auth = FirebaseAuth.getInstance()

        val signInButton = findViewById<Button>(R.id.btnGoogleSignIn)
        signInButton.setOnClickListener {
            val options  = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken( getString(R.string.webclient_id)).requestEmail().build()
            signInClient = GoogleSignIn.getClient(this, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
        val logout = findViewById<Button>(R.id.btnGoogleSignOut)
        logout.setOnClickListener {
            if(isLoggedIn){
                signInClient.signOut().addOnCompleteListener {
                    isLoggedIn = false
                    val intent= Intent(this, MainActivity::class.java)
                    Toast.makeText(this,"Logging Out",Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }
            }
            else{
                Toast.makeText(this, "Cannot log out you're not logged in", Toast.LENGTH_LONG).show()
            }
        }
    }

        private fun googleAuthForFirebase(account : GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    val current = auth.currentUser

                    Toast.makeText(this@TempLoginActivity, "Successfully logged in", Toast.LENGTH_LONG).show()
                    isLoggedIn = true
                }


            } catch(e : Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText( this@TempLoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }


}
