package com.example.fcm_test_andriod.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.fcm_test_andriod.R;
import com.example.fcm_test_andriod.activity.DetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "FCM Data Payload: " + remoteMessage.getData().toString());

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String senderNickname = remoteMessage.getData().get("senderNickname");
            String scheduledDate = remoteMessage.getData().get("scheduledDate");

            // ✅ Foreground Service 실행하여 알림(Notification) 생성
            Intent serviceIntent = new Intent(this, AlarmService.class);
            serviceIntent.putExtra("title", title);
            serviceIntent.putExtra("body", body);
            serviceIntent.putExtra("senderNickname", senderNickname);
            serviceIntent.putExtra("scheduledDate", scheduledDate);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private void sendNotification(String title, String messageBody, PendingIntent pendingIntent) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // ✅ Android 8.0 이상에서는 NotificationChannel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "High Priority Channel",
                    NotificationManager.IMPORTANCE_HIGH // 🔥 HIGH 설정 필요
            );
            channel.setDescription("This is a high priority channel for important notifications.");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // 🔥 잠금화면에서도 표시
            notificationManager.createNotificationChannel(channel);
        }

        // ✅ 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 🔥 HIGH 설정
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true); // 🔥 전체화면 실행

        // ✅ 알림 표시 (고유 ID 사용)
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // 새 토큰이 생성되면 서버에 전달하는 등의 처리를 여기서 수행
    }
}
