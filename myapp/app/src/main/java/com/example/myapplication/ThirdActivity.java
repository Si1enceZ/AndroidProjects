package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ThirdActivity extends AppCompatActivity {
    private static final String TAG = "ThirdActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);
        Button button1 = (Button) findViewById(R.id.button_4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=getIntent();
                String data = intent.getStringExtra("Test Data");
                Log.d(TAG, "onClick: get the data:"+data);
                Toast.makeText(ThirdActivity.this,data,Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}