package com.github.displace.sdp2022

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val textView =
            findViewById<TextView>(R.id.greetingMessage).apply { text = "Hello " + message +"!" }
    }
}