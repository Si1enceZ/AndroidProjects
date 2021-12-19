package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class HelloWorldActivity extends AppCompatActivity {
    private static final String TAG = "HelloWorldActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(HelloWorldActivity.this,"First Item Selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                Toast.makeText(HelloWorldActivity.this,"Second Item Selected",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: FirstActivity");
        if(savedInstanceState!=null){
            Log.d(TAG, "get the message from the last"+savedInstanceState.getString("dataRemained"));
        }
        setContentView(R.layout.activity_hello_world);
        Log.d("HelloWorldActivity", String.valueOf(R.string.app_name));
        Button button1 =(Button) findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(HelloWorldActivity.this,"HelloWorld Test!",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(HelloWorldActivity.this,HelloWorldActivity.class);
                Intent intent = new Intent("com.example.myapplication.secondActivity");
                intent.addCategory("android.intent.category.DEFAULT");


                startActivityForResult(intent,1);
            }
        });
        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.myapplication.thirdActivity");
                //intent.setData(Uri.parse("htttp://www.baidu.com"));
                Log.d(TAG, "put the message:666");
                intent.putExtra("Test Data","6666");

                startActivity(intent);
                finish();
            }
        });

        RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String dataReturn = data.getStringExtra("Return Data");
                    Log.d(TAG, "onActivityResult: "+dataReturn);    
                }
                else{
                    Log.i(TAG, "onActivityResult: something wrong");
                }
                break;
                
        }
    }

    @Override
    public  void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String tempData = "last data2";
        outState.putString("dataRemained",tempData);
        Log.d(TAG, "onSaveInstanceState: Remained data:"+tempData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: FirstActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: FirstActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: FirstActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: FirstActivity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: FirstActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: FirstActivity");
    }
}