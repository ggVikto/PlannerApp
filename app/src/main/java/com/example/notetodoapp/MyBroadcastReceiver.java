package com.example.notetodoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        String title = "Напоминание о событии";
        String body = "Событие: " + eventName;
        NotificationUtils.displayNotification(context, title, body);
    }
}

