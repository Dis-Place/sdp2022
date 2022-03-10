package com.github.blecoeur.bootcamp

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ProfileActivity : AppCompatActivity() {

    val dummyAchList : List<String>  = listOf("ach1","ach2","ach3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAch)
        val listener = object : ClickListener() {
            override fun click(index: Int) {

            }
        }
        val adapter = AchAdapter( applicationContext ,listener, dummyAchList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext);
    }


    fun button( view : View){
        findViewById<ScrollView>(R.id.FriendsScroll).visibility = View.GONE
        findViewById<ScrollView>(R.id.InboxScroll).visibility = View.GONE
    }
    fun button2( view : View) {
    }
}