package com.github.displace.sdp2022.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.profile.messages.MessageHandler

class NewsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsRecyclerView = findViewById<RecyclerView>(R.id.recyclerNews)
        val newsAdapter = NewsViewAdapter(applicationContext, getNewsList())
        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)


    }

    private fun getNewsList(): List<News> {
        return listOf(
            News(
                "WEEKLY UPDATE",
                "Profiles and News are now also available!",
                "14/03/2022"
            )
        )
    }


}