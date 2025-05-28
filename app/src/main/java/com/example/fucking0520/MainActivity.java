package com.example.fucking0520;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //변수 선언
    private EditText j_emailEditText, j_passwordEditText;
    private TextView j_statusText;
    private Button j_loginButton, j_registerButton;

    private Button j_btn_main;

    String tag = "LoginCheck";

    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        j_emailEditText = findViewById(R.id.editTextID);
        j_passwordEditText = findViewById(R.id.editTextPassword);
        j_statusText = findViewById(R.id.textView4);
        j_loginButton = findViewById(R.id.buttonLogin);
        j_registerButton = findViewById(R.id.buttonRegister);

        j_loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //아이디와 비밀번호를 string 변수에 대입
                String email = j_emailEditText.getText().toString();
                String password = j_passwordEditText.getText().toString();

                //Log.d(tag, "입력 아이디:" + email);
                //Log.d(tag, "입력 아이디:" + password);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("ID", email);
                intent.putExtra("Password", password);
                //런처 실행
                launcher.launch(intent);

            }
        });
        //launcher

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode()== Activity.RESULT_OK) {
                        Intent data = result.getData();
                        j_statusText.setText(data.getStringExtra("status"));
                    }

                    //j_statusText = 로그인성공 -> 버튼 보이게 if문으로
                    if("로그인성공".equals(j_statusText.getText().toString())){
                        Intent intent = new Intent(MainActivity.this, IntentActivity.class);
                        startActivity(intent);
                    }
                });

    }

    public void nextregisterpage(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}