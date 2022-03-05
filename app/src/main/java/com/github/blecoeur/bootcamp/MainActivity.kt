package com.github.blecoeur.bootcamp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_MESSAGE = "com.github.blecoeur.bootcamp.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAppUse(view: View) {
        val nameText = findViewById<EditText>(R.id.mainName)
        val name = nameText.text.toString()

        val intent =
            Intent(this, MainMenuActivity::class.java).apply { putExtra(EXTRA_MESSAGE, name) }
        startActivity(intent)
    }

    fun quitApp(view: View) {

    }

}