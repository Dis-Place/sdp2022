package com.github.displace.sdp2022.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.blecoeur.bootcamp.MyApplication
import com.github.blecoeur.bootcamp.R

class NewsActivity : AppCompatActivity() {

    private lateinit var dbAccess : NewsDbConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val app = applicationContext as MyApplication
        dbAccess = app.getNewsDb()

        val newsRecyclerView = findViewById<RecyclerView>(R.id.recyclerNews)
        val newsAdapter = NewsViewAdapter( applicationContext , dbAccess.getNewsList(3))
        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

    }
}