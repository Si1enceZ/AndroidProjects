package com.example.databasetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";
    private static final String CREATE_BOOK = "create table BOOK ("
            +"id integer primary key autoincrement,"
            +"author text,"
            +"price real,"
            +"pages integer,"
            +"name text)";

    private static final String CREATE_CATEGORY ="create table Category ("
            +"id integer primary key autoincrement,"
            +"category_name text,"
            +"category_code integer)";

    private static final String DROP_BOOK = "drop table if exists Book";
    private static final String DROP_CATEGORY = "drop table if exists Category";
    private Context context;

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Log.d(TAG, "onCreate: Create Succeeded");
        Toast.makeText(context,"Create Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_BOOK);
        db.execSQL(DROP_CATEGORY);
        onCreate(db);
    }
}
