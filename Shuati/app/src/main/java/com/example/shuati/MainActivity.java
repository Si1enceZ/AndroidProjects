package com.example.shuati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private String[] problem=new String[]{};
    private String[] question = new String[]{};
    private String[] answer = new String[]{};

    private TextView infoText, problemText ,answerText;
    private RadioButton buttonA,buttonB,buttonC,buttonD;
    private int num=0,idx;
    private final List<Integer> right = new ArrayList<>();
    private final List<Integer> wrong = new ArrayList<>();


    private int allNumber =0;
    private int panNumber =0;
    private String userChoice;
    private static final String TAG = "MainActivity";

    public String loadFile(String fileName) throws IOException {

        InputStream is = null;
        String text=null;
        try {
            is=this.getResources().getAssets().open(fileName);
            byte[] bytes= new byte[is.available()];
            is.read(bytes);
            text=new String(bytes);
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "loadFile: load file failed" );
        }finally {
            try {
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Log.d(TAG, "loadFile: load file: "+fileName+" Successfully");
        return text;

        /*
        StringBuilder builder=new StringBuilder();
        BufferedReader bufferedReader=null;
        try{
            bufferedReader=new BufferedReader(new InputStreamReader(openFileInput(fileName)));//File is put in /data/data/com.example.shuati/files
            String line;
            while((line=bufferedReader.readLine())!=null){
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                    Log.d(TAG, "loadFile: Success load File:"+fileName);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
        */

    }

    public void printFunc() {
        String info = String.format(this.getString(R.string.info),num+1,allNumber,right.size(),wrong.size());
        infoText.setText(info);
        idx = (int) (Math.random() * allNumber);
        while (right.contains(idx) || wrong.contains(idx)) {
            idx = (int) (Math.random() * allNumber);
        }
        problemText.setText(problem[idx]);

        if (idx >= panNumber) {
            String[] choices = question[idx-panNumber].split("\t"); // In ”question.txt“, The choice is split by '\t'
            buttonA.setText(choices[0]);
            buttonB.setText(choices[1]);
            buttonC.setText(choices[2]);
            buttonD.setText(choices[3]);
        } else {
            buttonA.setText("对");
            buttonB.setText("错");
            buttonC.setText("");
            buttonD.setText("");
        }

        Log.d(TAG, "printFunc: print the problem:"+idx);
    }


    public void reviewFunc(){
        if(wrong.size()<=0){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Congratulations!");
            alertDialog.setMessage("You Finished all the Problem.\nDo you want to do it again?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();

            return;
        }
        String info = String.format(this.getString(R.string.info),num+1,allNumber,right.size(),wrong.size());
        infoText.setText(info);
        idx =wrong.get(0);
        wrong.remove(0);

        problemText.setText(problem[idx]);

        if (idx >= panNumber) {
            String[] choices = question[idx-panNumber].split("\t"); // In ”question.txt“, The choice is split by '\t'
            buttonA.setText(choices[0]);
            buttonB.setText(choices[1]);
            buttonC.setText(choices[2]);
            buttonD.setText(choices[3]);
        } else {
            buttonA.setText("对");
            buttonB.setText("错");
            buttonC.setText("");
            buttonD.setText("");
        }
        Log.d(TAG, "reviewFunc: print the problem: "+idx);
    }
    public void initFunc(){
        /*
            init function
            set the allNumber and the panNumber
         */
        allNumber = problem.length;
        panNumber = allNumber - question.length;

        printFunc();
        Log.d(TAG, "initFunc: init Successfully. allNumber: "+allNumber+" panNumber: "+panNumber);
    }



    public void checkFunc(){
        /*
            check if the user choice is right
         */



        Log.d(TAG, "checkFunc: userChoice: "+userChoice+" answer: "+answer[idx].charAt(0));
        if(userChoice.charAt(0)==answer[idx].charAt(0)){
            answerText.setText("Great!");
            right.add(idx);
        }else{
            answerText.setText( "Wrong! The True Answer is: "+answer[idx]);
            wrong.add(idx);
        }
        num++;
        if(num<allNumber){
            printFunc();
        }else{
            reviewFunc();
        }

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText =(TextView)findViewById(R.id.infoText);
        problemText = (TextView) findViewById(R.id.problemText);
        answerText = (TextView) findViewById(R.id.answerText);
        buttonA=(RadioButton)findViewById(R.id.button_A);
        buttonB=(RadioButton)findViewById(R.id.button_B);
        buttonC=(RadioButton)findViewById(R.id.button_C);
        buttonD=(RadioButton)findViewById(R.id.button_D);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        try {
            problem=loadFile("problem.txt").split("\n");
            question=loadFile("question.txt").split("\n");
            answer=loadFile("answer.txt").split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        initFunc();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /*
                get which choice you did
             */
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.button_A:
                        userChoice="A";
                        break;
                    case R.id.button_B:
                        userChoice="B";
                        break;
                    case R.id.button_C:
                        userChoice="C";
                        break;
                    case R.id.button_D:
                        userChoice="D";
                        break;
                    default:
                        Log.i(TAG, "onCheckedChanged: failed get the answer");
                }
                Log.d(TAG, "onCheckedChanged: user choose the "+userChoice);
            }
        });

        Button button1 = (Button)findViewById(R.id.button_submit);
        button1.setOnClickListener(v -> checkFunc());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        right.clear();
        wrong.clear();

    }
}