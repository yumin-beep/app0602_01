package com.example.fucking0520;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IntentActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 2000;

    LinearLayout layout;

    String msg = "Tag: ";
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

        layout = (LinearLayout) findViewById(R.id.layout);
    }

    public void onClicked_intent(View view){
        Intent intent = null;

        if(view.getId() == R.id.btn_web){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
        }

        if(view.getId() == R.id.btn_call){
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-5678"));
        }

        if(view.getId() == R.id.btn_map){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.30, 127.2?z=20"));
        }

        if(view.getId() == R.id.btn_num){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"));
        }

        if(view.getId() == R.id.btn_memo){
            intent = new Intent(IntentActivity.this, MemoActivity.class);
            Log.d(msg, "메모버튼");

        }

        if(intent != null){
            startActivity(intent);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.blue){
            layout.setBackgroundColor(Color.BLUE);
            return true;
        }
        else if(id==R.id.green){
            layout.setBackgroundColor(Color.GREEN);
            return true;
        }
        else if(id==R.id.red){
            layout.setBackgroundColor(Color.RED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}