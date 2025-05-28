package com.example.fucking0520;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class IntentActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = "위치";

    private LinearLayout layout;
    private TextView tvLocation;
    private TextView ctTime;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Location anchorLocation = null;
    private boolean isCounting = false;
    private long accumulatedTime = 0;
    private long lastStartTime = 0;
    private Handler timerHandler = new Handler();

    @SuppressLint("MissingInflatedId")
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLocations().isEmpty()) {
                    tvLocation.setText("위치 정보를 가져올 수 없습니다.");
                    return;
                }

                Location current = locationResult.getLastLocation();
                double lat = current.getLatitude();
                double lon = current.getLongitude();
                String msg = "위도: " + lat + "\n경도: " + lon;
                tvLocation.setText(msg);
                Log.d("위치결과", msg);

                if (anchorLocation != null) {
                    float distance = current.distanceTo(anchorLocation);
                    if (distance <= 500) {
                        if (!isCounting) {
                            lastStartTime = System.currentTimeMillis();
                            isCounting = true;
                        }
                    } else {
                        if (isCounting) {
                            accumulatedTime += System.currentTimeMillis() - lastStartTime;
                            isCounting = false;
                        }
                    }
                }
            }
        };
    }

    public void onClicked_intent(View view) {
        Log.d("디버깅", "onClicked_intent 진입");
        Intent intent = null;
        int id = view.getId();

        if (id == R.id.btn_web) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
        } else if (id == R.id.btn_call) {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-5678"));
        } else if (id == R.id.btn_map) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.30, 127.2?z=20"));
        } else if (id == R.id.btn_num) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"));
        } else if (id == R.id.btn_memo) {
            intent = new Intent(IntentActivity.this, MemoActivity.class);
        } else if (id == R.id.btn_me) {
            requestLocationPermissionsAndFetch();
            return;
        } else if (id == R.id.btn_counttime) {
            showAccumulatedTime();
            return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void showAccumulatedTime() {
        if (isCounting) {
            accumulatedTime += System.currentTimeMillis() - lastStartTime;
            lastStartTime = System.currentTimeMillis();
        }
        long seconds = accumulatedTime / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        ctTime.setText("누적된 시간: " + minutes + "분 " + remainingSeconds + "초");
    }

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissionsAndFetch() {
        if (hasLocationPermissions()) {
            fetchLocation();
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION
        );
    }

    private void fetchLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        anchorLocation = location;
                        accumulatedTime = 0;
                        isCounting = false;
                        tvLocation.setText("기준 위치 설정됨\n위도: " + location.getLatitude() + "\n경도: " + location.getLongitude());
                    }
                });

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
                fetchLocation();
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
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
