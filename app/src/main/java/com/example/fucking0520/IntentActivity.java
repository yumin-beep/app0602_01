// ✅ IntentActivity.java (토글 방식으로 위치 추적 시작/중단 및 시간 리셋)
package com.example.fucking0520;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IntentActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private LinearLayout layout;
    private TextView tvLocation;
    private TextView ctTime;
    private Button btnMe;
    private SharedPreferences prefs;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_intent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layout = findViewById(R.id.layout);
        tvLocation = findViewById(R.id.tv_location);
        ctTime = findViewById(R.id.ct_time);
        btnMe = findViewById(R.id.btn_me);
        progressBar = findViewById(R.id.progress_time);
        SharedPreferences loginPrefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String userId = loginPrefs.getString("logged_in_id", "default_user");

        // ✅ 유저별 SharedPreferences 지정
        prefs = getSharedPreferences("location_timer_" + userId, MODE_PRIVATE);


        updateTrackingButton();
    }

    public void onClicked_intent(View view) {
        int id = view.getId();
        Intent intent = null;

        if (id == R.id.btn_web) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
        } else if (id == R.id.btn_call) {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-5678"));
        } else if (id == R.id.btn_map) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.30,127.2?z=20"));
        } else if (id == R.id.btn_num) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"));
        } else if (id == R.id.btn_memo) {
            intent = new Intent(this, MemoActivity.class);
        } else if (id == R.id.btn_me) {
            toggleLocationTracking();
            return;
        } else if (id == R.id.btn_counttime) {
            showAccumulatedTime();
            return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void toggleLocationTracking() {
        boolean isTracking = prefs.getBoolean("isTracking", false);
        SharedPreferences.Editor editor = prefs.edit();

        if (!isTracking) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                boolean fgsLoc = ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (!fine || !fgsLoc) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION
                    }, REQUEST_LOCATION);
                    return;
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_LOCATION);
                    return;
                }
            }

            editor.putBoolean("isTracking", true);
            editor.apply();

            Intent intent = new Intent(this, LocationForegroundService.class);
            intent.setAction("SET_ANCHOR");
            ContextCompat.startForegroundService(this, intent);

            Toast.makeText(this, "위치 추적 시작됨", Toast.LENGTH_SHORT).show();
            tvLocation.setText("위치 추적 중...");
        } else {
            editor.putBoolean("isTracking", false);
            editor.putBoolean("isCounting", false);
            editor.putLong("lastStartTime", 0);
            editor.putLong("accumulatedTime", 0);
            editor.apply();

            stopService(new Intent(this, LocationForegroundService.class));
            Toast.makeText(this, "위치 추적 중단 + 누적 시간 초기화", Toast.LENGTH_SHORT).show();
            ctTime.setText("누적 시간: 0분 0초");
            tvLocation.setText("위치 추적이 꺼졌습니다.");
        }

        updateTrackingButton();
    }

    private void updateTrackingButton() {
        boolean isTracking = prefs.getBoolean("isTracking", false);
        if (btnMe != null) {
            btnMe.setText(isTracking ? "위치 추적 중단" : "위치 추적 시작");
        }
    }

    private void showAccumulatedTime() {
        long accumulated = prefs.getLong("accumulatedTime", 0);
        long lastStart = prefs.getLong("lastStartTime", 0);

        if (prefs.getBoolean("isCounting", false) && lastStart > 0) {
            accumulated += System.currentTimeMillis() - lastStart;
        }

        long totalSec = accumulated / 1000;
        long minutes = totalSec / 60;
        long seconds = totalSec % 60;

        // 게이지 계산
        final int MAX_MINUTES = 10;
        float ratio = Math.min(1f, totalSec / 600f); // 600초가 최대치
        int percent = Math.round(ratio * 100);

        progressBar.setProgress(percent);

        if (ratio >= 1f) {
            ctTime.setText("🎉 미션 클리어!");
        } else {
            ctTime.setText("누적 시간: " + minutes + "분 " + seconds + "초 (" + percent + "%)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            boolean fineGranted = false;
            boolean fgsGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    fineGranted = true;
                if (permissions[i].equals(Manifest.permission.FOREGROUND_SERVICE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    fgsGranted = true;
            }

            if (fineGranted && (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || fgsGranted)) {
                toggleLocationTracking();
            } else {
                Toast.makeText(this, "위치 권한 또는 서비스 권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.blue) {
            layout.setBackgroundColor(Color.BLUE);
        } else if (id == R.id.green) {
            layout.setBackgroundColor(Color.GREEN);
        } else if (id == R.id.red) {
            layout.setBackgroundColor(Color.RED);
        }
        return true;
    }
}
