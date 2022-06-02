package com.github.displace.sdp2022.profile.messages

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.github.displace.sdp2022.MainMenuActivity
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.RealTimeDatabase
import com.github.displace.sdp2022.database.DatabaseFactory
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.users.PartialUser
import com.github.displace.sdp2022.util.listeners.Listener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MessageHandler(private val activePartialUser : PartialUser, app : MyApplication , intent : Intent ) {

    private var msgLs : List<Message> = app.getActiveUser()!!.getMessageHistory()

    private val db = DatabaseFactory.getDB(intent)

    private val context = app


    init{
        checkForNewMessages()
    }




    /**
     * Send a notification
     * @param title : title of the notification
     * @param content : content of the notification
     */
    fun messageNotification(title : String, content : String) {
        val channelId = "i.apps.notifications"
        val description = "Test notification"
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel: NotificationChannel
        val builder: Notification.Builder

        val intent = Intent(context, ProfileActivity::class.java)

        // FLAG_UPDATE_CURRENT specifies that if a previous
        // PendingIntent already exists, then the current one
        // will update it with the latest intent
        // 0 is the request code, using it later with the
        // same method again will get back the same pending
        // intent for future reference
        // intent passed here is to our afterNotification class
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    /**
     * Checks if any new messages have arrived
     * Send notifications for all the new messages
     */
    fun checkForNewMessages(){
        db.getThenCall<List<Map<String,Any>>>("CompleteUsers/" + activePartialUser.uid + "CompleteUser/MessageHistory"){ ls ->
            val tempList : List<Message>
            if(ls != null){
                tempList = getListOfMessages(ls)
                if(msgLs.isEmpty()){
                    //setup of the listener for the first time : no notification must be sent
                    msgLs = tempList
                }else if(tempList != msgLs){
                    //notification of the new messages
                    val diff = tempList.filter { it !in msgLs }
                    for(msg in diff){
                        messageNotification(msg.sender.username,msg.message)
                    }
                    msgLs = tempList
                }
            }
        }
    }

    /**
     * Transforms the data of the database into a list of messages to
     */
    fun getListOfMessages(maps: List<Map<String,Any>>) : List<Message> {
        var arr : List<Message> = arrayListOf()
        for( map in maps ){
            val sender = map["sender"] as Map<String,Any>
            val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
            arr = arr + m
        }
        return arr
    }


}