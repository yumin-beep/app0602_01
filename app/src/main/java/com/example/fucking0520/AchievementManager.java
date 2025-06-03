package com.example.fucking0520;

import android.content.Context;
import android.content.SharedPreferences;

public class AchievementManager {

    // 업적 저장
    public static void saveAchievement(Context context, String username, String missionId, boolean cleared) {
        SharedPreferences prefs = context.getSharedPreferences("achievements_" + username, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(missionId, cleared);
        editor.apply();
    }

    // 업적 불러오기
    public static boolean loadAchievement(Context context, String username, String missionId) {
        SharedPreferences prefs = context.getSharedPreferences("achievements_" + username, Context.MODE_PRIVATE);
        return prefs.getBoolean(missionId, false);
    }

    // 특정 유저의 모든 업적을 초기화하고 싶을 때
    public static void resetAllAchievements(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences("achievements_" + username, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // 예시: 업적 개수에 따라 전체 로드
    public static boolean[] getAllAchievements(Context context, String username, int missionCount) {
        boolean[] results = new boolean[missionCount];
        SharedPreferences prefs = context.getSharedPreferences("achievements_" + username, Context.MODE_PRIVATE);

        for (int i = 0; i < missionCount; i++) {
            results[i] = prefs.getBoolean("mission_" + (i + 1), false);
        }
        return results;
    }
}
