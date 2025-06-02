// ✅ LocationForegroundService.java (수정된 전체 코드)
package com.example.fucking0520;

import android.app.*;
import android.content.*;
import android.location.Location;
import android.os.*;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.*;

public class LocationForegroundService extends Service {

    private static final String CHANNEL_ID = "location_channel";
    private static final int NOTIF_ID = 1;
    private static final float RADIUS = 500f;
    private static final String PREFS = "location_timer";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Location anchorLocation;
    private boolean isCounting = false;
    private long lastStartTime = 0;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loadAnchor();
        startForegroundServiceNotif();
        startLocationUpdates();
    }

    private void startForegroundServiceNotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "위치 추적", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("위치 추적 중")
                .setContentText("앱이 500m 이내 체류 시간을니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(NOTIF_ID, notification);
    }

    private void loadAnchor() {
        double lat = Double.longBitsToDouble(prefs.getLong("anchorLat", 0));
        double lon = Double.longBitsToDouble(prefs.getLong("anchorLon", 0));
        if (lat != 0 || lon != 0) {
            anchorLocation = new Location("saved");
            anchorLocation.setLatitude(lat);
            anchorLocation.setLongitude(lon);
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Location current = result.getLastLocation();
                if (anchorLocation != null && current != null) {
                    float distance = current.distanceTo(anchorLocation);
                    SharedPreferences.Editor editor = prefs.edit();
                    long accumulatedTime = prefs.getLong("accumulatedTime", 0);

                    Log.d("위치서비스", "현재 거리: " + distance + "m, isCounting: " + isCounting);

                    if (distance <= RADIUS) {
                        if (!isCounting) {
                            lastStartTime = System.currentTimeMillis();
                            isCounting = true;
                            editor.putBoolean("isCounting", true);
                            editor.putLong("lastStartTime", lastStartTime);
                            editor.apply();
                            Log.d("위치서비스", "500m 이내 진입 → 시간 누적 시작");
                        }
                    } else {
                        if (isCounting) {
                            accumulatedTime += System.currentTimeMillis() - lastStartTime;
                            editor.putLong("accumulatedTime", accumulatedTime);
                            editor.putBoolean("isCounting", false);
                            editor.putLong("lastStartTime", 0);
                            editor.apply();
                            isCounting = false;
                            Log.d("위치서비스", "500m 벗어남 → 시간 누적 종료, 총 누적(ms): " + accumulatedTime);
                        }
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("SET_ANCHOR".equals(intent.getAction())) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    anchorLocation = location;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("anchorLat", Double.doubleToLongBits(location.getLatitude()));
                    editor.putLong("anchorLon", Double.doubleToLongBits(location.getLongitude()));
                    editor.putLong("accumulatedTime", 0);
                    editor.putBoolean("isCounting", false);
                    editor.putLong("lastStartTime", 0);
                    editor.apply();
                }
            });
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback);

        // 👉 상태가 "실제로 카운팅 중"일 때만 누적 시간 저장
        boolean isCounting = prefs.getBoolean("isCounting", false);
        long lastStartTime = prefs.getLong("lastStartTime", 0);
        long accumulatedTime = prefs.getLong("accumulatedTime", 0);

        if (isCounting && lastStartTime != 0) {
            accumulatedTime += System.currentTimeMillis() - lastStartTime;
            prefs.edit().putLong("accumulatedTime", accumulatedTime).apply();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
