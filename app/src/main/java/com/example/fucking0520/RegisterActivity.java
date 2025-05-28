package com.example.fucking0520;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    String id, password;
    private EditText R_id, R_pw;
    private Button r_completebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        R_id = findViewById(R.id.registerid);
        R_pw = findViewById(R.id.registerpw);
        r_completebtn = findViewById(R.id.registercompletebutton);


    }
    public void onRegisterComplete(View v) {

        String inputId = R_id.getText().toString();
        String inputPw = R_pw.getText().toString();

        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("saved_id", inputId);
        editor.putString("saved_pw", inputPw);
        editor.apply();

        Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();
        finish();  // 또는 로그인 화면으로 이동
    }

}