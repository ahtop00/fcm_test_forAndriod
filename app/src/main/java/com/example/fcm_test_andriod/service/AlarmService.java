package com.example.fcm_test_andriod.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.fcm_test_andriod.R;
import com.example.fcm_test_andriod.activity.DetailActivity;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final String CHANNEL_ID = "AlarmServiceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AlarmService onStartCommand 실행됨");

        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String senderNickname = intent.getStringExtra("senderNickname");
        String scheduledDate = intent.getStringExtra("scheduledDate");

        // ✅ Foreground Service 실행 전에 NotificationChannel 생성
        createNotificationChannel();

        // ✅ 알림(Notification) 클릭 시 DetailActivity 실행
        Intent activityIntent = new Intent(this, DetailActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityIntent.putExtra("title", title);
        activityIntent.putExtra("body", body);
        activityIntent.putExtra("senderNickname", senderNickname);
        activityIntent.putExtra("scheduledDate", scheduledDate);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentIntent(pendingIntent) // 🔥 알림 클릭 시 DetailActivity 실행
                .setAutoCancel(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34 (Android 14)
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notification);
        }

        // ✅ Foreground Service는 실행하지만, 액티비티는 즉시 실행하지 않고 알림 클릭을 유도
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service",
                    NotificationManager.IMPORTANCE_HIGH // 🔥 중요도 HIGH로 설정하여 사용자에게 확실하게 알림
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
