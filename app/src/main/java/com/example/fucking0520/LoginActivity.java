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
            intent.putExtra("status", "로그인성공");
        }
        else{
            intent.putExtra("status","로그인실패");
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isUserValid(String username, String password){
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedId = prefs.getString("saved_id", "");
        String savedPw = prefs.getString("saved_pw", "");

        return username.equals(savedId) && password.equals(savedPw);
    }
}