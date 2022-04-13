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
import com.github.displace.sdp2022.users.PartialUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MessageHandler(val activePartialUser : PartialUser, app : MyApplication) {

    private var msgLs : ArrayList<Message> = arrayListOf()
    private val db : RealTimeDatabase = RealTimeDatabase().instantiate("https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/",false) as RealTimeDatabase
    private val context = app

    fun getList() : ArrayList<Message>{
        return msgLs
    }

    init{
        addListener()
    }

    private fun messageListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val ls = snapshot.value as ArrayList<HashMap<String,Any>>?
            val tempList : ArrayList<Message> = arrayListOf()
            if(ls != null){
                for( map in ls ){
                    val sender = map["sender"] as HashMap<String,Any>
                    val m = Message(map["message"] as String,map["date"] as String, PartialUser(sender["username"] as String,sender["uid"] as String) )
                    tempList.add(m)
                }
                if(msgLs.isEmpty()){
                    //setup of the list for the first time : no notification must be sent
                    msgLs = tempList
                }else if(tempList != msgLs){
                    //notification of the new message
                    messageNotification(tempList[0])
                    msgLs = tempList
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    fun messageNotification(message: Message) {
        val channelId = "i.apps.notifications"
        val description = "Test notification"
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel: NotificationChannel
        val builder: Notification.Builder

        val intent = Intent(context, MainMenuActivity::class.java)

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
                .setContentTitle(message.sender.username)
                .setContentText(message.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(context)
                .setContentTitle(message.sender.username)
                .setContentText(message.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    fun addListener(){
        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").addValueEventListener(messageListener())
    }

    fun removeListener(){
        db.getDbReference("CompleteUsers/" + activePartialUser.uid + "/MessageHistory").removeEventListener(messageListener())
    }


}