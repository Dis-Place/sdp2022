package com.github.displace.sdp2022.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.database.FirebaseDatabaseAdapter
import com.github.displace.sdp2022.database.GoodDB
import com.github.displace.sdp2022.profile.achievements.AchievementsLibrary
import com.github.displace.sdp2022.profile.messages.MessageHandler

/**
 * Activity showing the news of the application
 */
class NewsActivity : AppCompatActivity() {

    //private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private lateinit var db : GoodDB
    /**
     * Creates the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        db = DatabaseFactory.getDB(intent)

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()

        /**
         * Get the news from the database :
         */
        db.getThenCall< ArrayList<HashMap<String,Any>> >("News") { news ->
            val ls = mapToList(news)    //transform the database data into the News type
            showNews(ls)
        }

    }

    /**
     * Transforms the data received from the database into a list of News that can eb used by the Ui
     * @param news : the news as received from the database
     * @return the list of news
     */
    private fun mapToList(news : ArrayList<HashMap<String,Any>>? ) : List<News>{
        //default news list in case of error
        val ls = arrayListOf<News>(News(
            "NO NEWS FOUND",
            "It seems there was a problem getting the news, come back later!",
            "NOW",
            null
        ))
        if(news != null){   //if the list is null it means the list has not been found in the database
            ls.clear()  //no need for the default anymore , we can safely clear it
            for(map in news){   //transform the map of the database into a list of news
                ls.add( News(map["title"] as String , map["description"] as String , map["date"] as String, (applicationContext as MyApplication).getActiveUser()?.getProfilePic() ))
            }
        }
        return ls
    }

    /**
     * Shows the news in the UI using the recycler view
     * @param ls : the list of news to show
     */
    private fun showNews( ls : List<News>){
        val newsRecyclerView = findViewById<RecyclerView>(R.id.recyclerNews)
        val newsAdapter = NewsViewAdapter(applicationContext, ls)
        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val app = applicationContext as MyApplication
        AchievementsLibrary.achievementCheck(app.getActiveUser()!!,true,
            AchievementsLibrary.newsLib)
    }

    override fun onResume() {
        super.onResume()

        val app = applicationContext as MyApplication
        app.getMessageHandler().checkForNewMessages()
    }


}