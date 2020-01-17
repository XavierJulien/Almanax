package com.jxavier.almanax.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jxavier.almanax.MainActivity;
import com.jxavier.almanax.R;
import com.jxavier.almanax.Utils;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentToRepeat = new Intent(context, MainActivity.class);

        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
    }


    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context,Utils.CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.dofus)
                        .setContentTitle("Almanax")
                        .setContentText("Oublie pas de faire ton almanax du jour !")
                        .setAutoCancel(true);
    }
}
