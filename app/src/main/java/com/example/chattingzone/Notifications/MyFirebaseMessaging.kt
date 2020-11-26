package com.example.chattingzone.Notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.chattingzone.MessageChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {

    @SuppressLint
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sent = remoteMessage.data["sent"]
        val user = remoteMessage.data["user"]
        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharedPref.getString("CurrentUser", "none")
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sent == firebaseUser.uid) {
            if (currentOnlineUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage)
                } else {
                    sendNotification(remoteMessage)
                }
            }
        }

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification=remoteMessage.notification
        val j=user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent=Intent(this,MessageChatActivity::class.java)

        val bundle=Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

       val builder:NotificationCompat.Builder=NotificationCompat.Builder(this)
           .setSmallIcon(icon!!.toInt())
           .setContentTitle(title)
           .setContentText(body)
           .setAutoCancel(true)
           .setSound(defaultSound)
           .setContentIntent(pendingIntent)

        val noti=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i=0
        if (j>0){
            i=j
        }
        noti.notify(i,builder.build())
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification=remoteMessage.notification
        val j=user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent=Intent(this,MessageChatActivity::class.java)

        val bundle=Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreaoNotification=OreoNotifications(this)
        val builder:Notification.Builder=oreaoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon)

        var i=0
        if (j>0){
            i=j
        }
        oreaoNotification.getManager!!.notify(i,builder.build())
    }
}