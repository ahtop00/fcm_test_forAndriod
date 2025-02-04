package com.example.fcm_test_andriod.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.fcm_test_andriod.R;
import com.example.fcm_test_andriod.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // 메시지에 알림 페이로드가 있는 경우 처리
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Log.d(TAG, "Notification Title: " + notification.getTitle());
            Log.d(TAG, "Notification Body: " + notification.getBody());
            sendNotification(notification.getTitle(), notification.getBody());
        }
    }

    private void sendNotification(String title, String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0 이상에서는 NotificationChannel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) { // 채널이 없으면 생성
                channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Default Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Channel for default notifications");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 알림 클릭 시 열릴 액티비티 지정 (예시로 MainActivity 사용)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)  // 알림 아이콘
                .setContentTitle(title)                                     // 알림 제목
                .setContentText(messageBody)                                // 알림 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)           // 우선순위
                .setContentIntent(pendingIntent)                            // 클릭 시 실행할 인텐트
                .setAutoCancel(true);                                       // 알림 클릭 시 자동 제거

        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // 새 토큰이 생성되면 서버에 전달하는 등의 처리를 여기서 수행
    }
}
