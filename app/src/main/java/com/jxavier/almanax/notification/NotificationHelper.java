package com.jxavier.almanax.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {
    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;

    public static void scheduleRepeatingRTCNotification(Context context, int hour, int min) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);

        Intent intent = new Intent(context, AlarmReceiver.class);

        alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManagerRTC = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManagerRTC.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC);
    }

    public static void cancelAlarmRTC() {
        if (alarmManagerRTC != null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}