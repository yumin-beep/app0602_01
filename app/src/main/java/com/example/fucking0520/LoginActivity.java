package com.example.fucking0520;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    String id, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        if(intent != null){
            id = intent.getStringExtra("ID");
            password = intent.getStringExtra("Password");

            //로그인 자동실행
            check(null);
        }


    }

    public void check(View v){
        Intent intent = new Intent();
        if(isUserValid(id, password)){
            // 🔥 로그인 성공 시 현재 로그인한 유저 ID 저장
            SharedPreferences loginPrefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
            loginPrefs.edit().putString("logged_in_id", id).apply();

            intent.putExtra("status", "로그인성공");
        } else {
            intent.putExtra("status","로그인실패");
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isUserValid(String username, String password){
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedPw = prefs.getString("user_" + username + "_pw", "");

        if (username.equals("") || !password.equals(savedPw)) return false;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("logged_in_id", username);  // 로그인한 유저 저장
        editor.apply();

        return true;
    }
}