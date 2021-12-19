package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ForthActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forth);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_forth:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ForthActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("this a AlertDialog");
                dialog.setTitle("Test Dialog");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ForthActivity.this,"OK",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ForthActivity.this,"Cancle",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

                ProgressDialog progressDialog = new ProgressDialog(ForthActivity.this);
                progressDialog.setTitle("This is a ProgressDialog");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(true);
                progressDialog.show();


                break;
        }
    }

}