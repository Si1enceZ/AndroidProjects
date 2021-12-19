package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        if(savedInstanceState!=null){
            Log.d(TAG, "get the message from the last"+savedInstanceState.getString("dataRemained1"));
        }
        Button button1=(Button)findViewById(R.id.button_3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this,"Closing this Activity",Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.putExtra("Return Data","data return successfully");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        intent.putExtra("Return Data","data return successfully");
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String tempData = "last data1";
        outState.putString("dataRemained1",tempData);
        Log.d(TAG, "onSaveInstanceState: Remained data:"+tempData);
    }
}