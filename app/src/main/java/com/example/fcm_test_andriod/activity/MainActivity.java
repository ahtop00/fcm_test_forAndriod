package com.example.fcm_test_andriod.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fcm_test_andriod.databinding.ActivityMainBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding; // 뷰 바인딩 객체

    // 권한 요청을 구분하기 위한 상수 선언
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1004;  // 고유한 숫자로 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 뷰 바인딩 객체 초기화
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // "topic"라는 주제에 구독
        FirebaseMessaging.getInstance().subscribeToTopic("topic").addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Log.d("FCM", "주제('topic')을 성공적으로 구독");
                } else {
                    Log.d("FCM", "주제('topic') 구독에 실패하였습니다.");
                }
            }
        });


        // Android 알림 권한 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.POST_NOTIFICATIONS }, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // 버튼 클릭 리스너 설정
        binding.buttonGetToken.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // FCM 토큰 요청
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "FCM 토큰 가져오기 실패", task.getException());
                                    return;
                                }

                                // 새 FCM 등록 토큰 가져오기
                                String token = task.getResult();
                                Log.d(TAG, "FCM 토큰 : " + token);

                                // 토큰을 화면에 표시
                                binding.textViewToken.setText("FCM Token : " + token);
                                binding.textViewToken.setVisibility(View.VISIBLE); // 토글로 TextView 표시
                            }
                        });
            }
        });
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_NOTIFICATION_PERMISSION) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "알림 권한이 허용 되었습니다.", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(this, "알림 권한이 거부 되었습니다.\n알림을 받을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
