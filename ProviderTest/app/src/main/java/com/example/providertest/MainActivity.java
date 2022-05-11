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



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private static final Uri targetUri = Uri.parse("content://com.example.databasetest.provider/book");
    private long newId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addData = findViewById(R.id.add_data);
        Button queryData = findViewById(R.id.query_data);
        Button updateData = findViewById(R.id.update_data);
        Button deleteData = findViewById(R.id.delete_data);

        addData.setOnClickListener(this);
        queryData.setOnClickListener(this);
        updateData.setOnClickListener(this);
        deleteData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_data:
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
                break;
            case R.id.query_data:
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
                break;
            case R.id.delete_data:
                uri = Uri.parse("content://com.example.databasetest.provider/book/"+newId);
                int deleteRows = getContentResolver().delete(uri,null,null);

                Log.d(TAG, "delete data : deleted rows: "+deleteRows);
                break;
            case R.id.update_data:
                values = new ContentValues();
                uri = Uri.parse("content://com.example.databasetest.provider/book/"+newId);
                values.put("pages",1120);
                int updateRows = getContentResolver().update(uri,values,"name = ?",new String[]{"A Clash of Kings"});
                Log.d(TAG, "update data: update rows: "+updateRows);
                break;
            default:
                break;
        }
    }
}