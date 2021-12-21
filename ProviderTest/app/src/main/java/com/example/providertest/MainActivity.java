package com.example.providertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final Uri targetUri = Uri.parse("content://com.example.databasetest.provider/book");
    private long newId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addData =(Button)findViewById(R.id.add_data);
        Button queryData =(Button)findViewById(R.id.query_data);
        Button updateData =(Button)findViewById(R.id.update_data);
        Button deleteData =(Button)findViewById(R.id.delete_data);

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                Uri uri = Uri.parse("content://com.example.databasetest.provider/book");
                values.put("name","A Clash of Kings");
                values.put("author","George Martin");
                values.put("pages",1040);
                values.put("price",22.85);
                Uri newUri = getContentResolver().insert(uri,values);

                //newId = newUri.getPathSegments().get(1);
                newId= ContentUris.parseId(newUri);
                Log.d(TAG, "add data: new Id "+newId);
            }
        });

        queryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=null;
                cursor = getContentResolver().query(targetUri,null,null,null,null);
                if(cursor!=null){
                    while (cursor.moveToNext()){
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));

                        Log.d(TAG, "onClick: book name is "+name);
                        Log.d(TAG, "onClick: book author is "+author);
                        Log.d(TAG, "onClick: book pages is "+pages);
                        Log.d(TAG, "onClick: book price is "+price);
                    }
                    cursor.close();
                }
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                Uri uri = Uri.parse("content://com.example.databasetest.provider/book/"+newId);
                values.put("pages",1120);
                int updateRows = getContentResolver().update(uri,values,"name = ?",new String[]{"A Clash of Kings"});
                Log.d(TAG, "update data: update rows: "+updateRows);
            }
        });

        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.example.databasetest.provider/book/"+newId);
                    int deleteRows = getContentResolver().delete(uri,null,null);

                Log.d(TAG, "delete data : deleted rows: "+deleteRows);
            }
        });
    }
}