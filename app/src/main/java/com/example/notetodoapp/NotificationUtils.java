package com.example.notetodoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {

    private static final String CHANNEL_ID = "my_channel_id"; // Идентификатор канала уведомлений

    public static void displayNotification(Context context, String title, String body) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            // Создание канала уведомлений для Android 8.0 (API Level 26) и выше
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "My Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // Создание и отправка уведомления
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_notification_icon);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());

        } else {
            // Уведомления отключены пользователем для данного приложения
            Utility.showToast(context, "Уведомления запрещены. Измените разрешения в настройках");
        }
    }
}
