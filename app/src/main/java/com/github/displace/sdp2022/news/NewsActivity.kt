package com.github.displace.sdp2022.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.messages.MessageHandler

class NewsActivity : AppCompatActivity() {

    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsRecyclerView = findViewById<RecyclerView>(R.id.recyclerNews)

        //get the news from the Database
        db.referenceGet("","News").addOnSuccessListener { snapshot ->
            val news = snapshot.value as ArrayList<HashMap<String,Any>>?
            val ls = arrayListOf<News>(News(
                "NO NEWS FOUND",
                "It seems there was a problem getting the news, come back later!",
                "NOW",
                null
            ))
            if(news != null){
                ls.clear()
                for(map in news){
                    ls.add( News(map["title"] as String , map["description"] as String , map["date"] as String, (applicationContext as MyApplication).getActiveUser()?.getProfilePic() ))
                }
            }
            val newsAdapter = NewsViewAdapter(applicationContext, ls)
            newsRecyclerView.adapter = newsAdapter
            newsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

            val app = applicationContext as MyApplication
            AchievementsLibrary.achievementCheck(app,app.getActiveUser()!!,true,
                AchievementsLibrary.newsLib)


        }.addOnFailureListener{
            val ls = arrayListOf<News>(News(
                "NO NEWS FOUND",
                "It seems you're offline, News aren't available !",
                "NOW",
                null
            ))
            val newsAdapter = NewsViewAdapter(applicationContext, ls)
            newsRecyclerView.adapter = newsAdapter
            newsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

    }


}