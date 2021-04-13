package com.gallosalocin.go4lunch.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.ui.MainActivity
import com.gallosalocin.go4lunch.util.Constants

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val name = intent.getStringExtra(Constants.NAME_EXTRA)
        val address = intent.getStringExtra(Constants.ADDRESS_EXTRA)
        val workmatesToday = intent.getStringExtra(Constants.WORKMATES_TODAY_EXTRA)
        val activityIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0)
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_MY_LUNCH_NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(name)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine(name)
                        .addLine(address)
                        .addLine(workmatesToday))
                .build()
        notificationManagerCompat.notify(Constants.NOTIFICATION_LUNCH_RESTAURANT_ID, notification)
    }
}