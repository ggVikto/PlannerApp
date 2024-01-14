package com.example.notetodoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class EventScheduler {

    public static void scheduleEventNotification(Context context, String eventName, long eventTimeInMillis) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.putExtra("eventName", eventName);
        int requestCode = 0;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, eventTimeInMillis, pendingIntent);
        }
    }
}

