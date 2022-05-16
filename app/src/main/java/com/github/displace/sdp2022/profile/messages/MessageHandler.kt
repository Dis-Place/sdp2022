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
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MessageHandler(private val activePartialUser : PartialUser, app : MyApplication) {

    private var msgLs : ArrayList<Message> = arrayListOf()
    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private val context = app

    init{
        checkForNewMessages()
    }

    /**
     * Listens for messages and sends a notification if anything new has been received
     */
    private fun messageListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            val tempList : ArrayList<Message>
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

        override fun onCancelled(error: DatabaseError) {
        }

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
     * adds the listener for the messages for the user : used to receive notifications
     * use a single event to check at the start and end of any activity
     */
    fun checkForNewMessages(){
        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").addListenerForSingleValueEvent(messageListener())
    }

    /**
     * Transforms the data of the database into a list of messages to
     */
    fun getListOfMessages(maps: ArrayList<HashMap<String,Any>>) : ArrayList<Message> {
        val arr : ArrayList<Message> = arrayListOf()
        for( map in maps ){
            val sender = map["sender"] as HashMap<String,Any>
            val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
            arr.add(m)
        }
        return arr
    }


}