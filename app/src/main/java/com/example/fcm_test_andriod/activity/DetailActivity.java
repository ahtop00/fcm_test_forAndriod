package com.example.fcm_test_andriod.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fcm_test_andriod.R;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ✅ 화면이 잠겨 있어도 표시되도록 설정
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // ✅ Intent에서 전달된 데이터 가져오기
        String title = getIntent().getStringExtra("title");
        String body = getIntent().getStringExtra("body");
        String senderNickname = getIntent().getStringExtra("senderNickname");
        String scheduledDate = getIntent().getStringExtra("scheduledDate");

        // ✅ UI에 데이터 표시
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView bodyTextView = findViewById(R.id.bodyTextView);
        TextView senderTextView = findViewById(R.id.senderTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);

        titleTextView.setText(title);
        bodyTextView.setText(body);
        senderTextView.setText("보낸 사람: " + senderNickname);
        dateTextView.setText("예약 날짜: " + scheduledDate);
    }
}
