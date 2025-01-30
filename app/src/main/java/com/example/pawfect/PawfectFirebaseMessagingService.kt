package com.example.pawfect

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appinterface.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PawfectFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received: ${remoteMessage.data}")
        val chatId = remoteMessage.data["chatId"] ?: ""

        FirebaseFirestore.getInstance()
            .collection("Chats").document(chatId)
            .collection("Messages")
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestMessage = documents.documents.last().toObject(Message::class.java)
                    if (!ChatScreenManager.isChatOpen(chatId)) {
                        sendNotification("New Message", latestMessage?.messageText ?: "", chatId)
                    }
                }
            }
    }

    private fun sendNotification(title: String, message: String, chatId: String) {
        val deepLinkIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("myapp://chat/$chatId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "chat_channel")
            .setSmallIcon(R.drawable.default_image)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

}

