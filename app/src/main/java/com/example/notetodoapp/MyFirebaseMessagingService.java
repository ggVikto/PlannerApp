package com.example.notetodoapp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Обработка входящего уведомления
        if (remoteMessage.getNotification() != null) {
            // Получение данных из уведомления
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            // Отображение уведомления на устройстве
            NotificationUtils.displayNotification(getApplicationContext(), title, body);
        }
    }
}
