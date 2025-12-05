package com.joke.mon.core.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.joke.mon.MainActivity
import com.joke.mon.R
import com.joke.mon.core.data.repository.SharePreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService()
{

    @Inject
    lateinit var sharePreferenceRepository: SharePreferenceRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버로 FCM 토큰 전송 (백엔드가 있다면)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        serviceScope.launch {
            val isPushEnabled = sharePreferenceRepository.getNotificationIsEnabled()
            if (!isPushEnabled) {
                return@launch
            }
            showLocalNotification(message)
        }
    }

    private fun showLocalNotification(message: RemoteMessage) {
        val notification = message.notification
        val title = notification?.title ?: message.data["title"] ?: "농담몬"
        val body = notification?.body ?: message.data["body"] ?: "농담몬과 함께 즐거운 하루 보내세요 😆"

        val channelId = "ai_bot_channel"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelName = "농담몬 알림"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)


        val intent = Intent(this, MainActivity::class.java).apply {
             flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            pendingIntentFlags
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}