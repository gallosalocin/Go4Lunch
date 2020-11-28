package com.gallosalocin.go4lunch.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.ui.MainActivity;

import static com.gallosalocin.go4lunch.util.Constants.ADDRESS_EXTRA;
import static com.gallosalocin.go4lunch.util.Constants.CHANNEL_MY_LUNCH_NOTIFICATION_ID;
import static com.gallosalocin.go4lunch.util.Constants.NAME_EXTRA;
import static com.gallosalocin.go4lunch.util.Constants.NOTIFICATION_LUNCH_RESTAURANT_ID;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_TODAY_EXTRA;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        String name = intent.getStringExtra(NAME_EXTRA);
        String address = intent.getStringExtra(ADDRESS_EXTRA);
        String workmatesToday = intent.getStringExtra(WORKMATES_TODAY_EXTRA);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_MY_LUNCH_NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(name)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(name)
                        .addLine(address)
                        .addLine(workmatesToday))
                .build();

        notificationManagerCompat.notify(NOTIFICATION_LUNCH_RESTAURANT_ID, notification);
    }

}
