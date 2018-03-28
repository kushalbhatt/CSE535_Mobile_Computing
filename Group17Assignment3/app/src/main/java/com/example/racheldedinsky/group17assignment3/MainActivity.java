package com.example.racheldedinsky.group17assignment3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.ProgressBar;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button train_button_var, test_button_var;
    int trainWalkCount, trainRunCount, trainJumpCount;
    boolean train_enabled=true;
    ProgressBar progressBar_walk_var, progressBar_run_var, progressBar_jump_var;
    TextView wait_for_not_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wait_for_not_clicked = (TextView)findViewById(R.id.waitText);
        wait_for_not_clicked.setText("");
        trainWalkCount=0;
        trainJumpCount=0;
        trainRunCount=0;
        initView();
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.train_button:
                if(train_enabled) {
                    int checked = ((RadioGroup) findViewById(R.id.rg1)).getCheckedRadioButtonId();
                    View radioButton = findViewById(checked);
                    int idx = ((RadioGroup) findViewById(R.id.rg1)).indexOfChild(radioButton);
                    //Toast.makeText(getApplicationContext(), idx+"", Toast.LENGTH_LONG).show();
                    String activity=new String();
                    boolean empty_string=false;
                    if (idx == 0) {
                        activity = "Walk";
                    } else if (idx == 1) {
                        activity = "Run";
                    } else if(idx==2) {
                        activity = "Jump";
                    }
                    else
                    {
                        empty_string=true;
                    }
                    try {
                        train_enabled=false;
                        train_button_var.setEnabled(train_enabled);
                        if (!(empty_string)) {
                            //Toast.makeText(getApplicationContext(), activity, Toast.LENGTH_LONG).show();
                            if (activity == "Walk") {
                                trainWalkCount++;
                                progressBar_walk_var.setProgress(0);
                                progressBar_walk_var.setProgress(trainWalkCount);
                                Integer array1[] = {1, 2, 3};
                                Integer array2[] = {4, 5, 6};
                                ArrayList<Integer[]> al = new ArrayList<Integer[]>();
                                al.add(array1);
                                al.add(array2);
                                //Toast.makeText(getApplicationContext(), "Arraylist contains: " + al.get(1)[1], Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), trainWalkCount + "", Toast.LENGTH_LONG).show();
                            } else if (activity == "Run") {
                                trainRunCount++;
                                progressBar_run_var.setProgress(0);
                                progressBar_run_var.setProgress(trainRunCount);
                                Toast.makeText(getApplicationContext(), trainRunCount + "", Toast.LENGTH_LONG).show();
                            } else if (activity == "Jump"){
                                trainJumpCount++;
                                progressBar_jump_var.setProgress(0);
                                progressBar_jump_var.setProgress(trainJumpCount);
                                Toast.makeText(getApplicationContext(), trainJumpCount + "", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Invalid data. Please enter the details correctly.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Invalid data. Please enter the details correctly.", Toast.LENGTH_SHORT).show();
                        }
                        activity = "";
                        train_enabled=true;
                        train_button_var.setEnabled(train_enabled);
                        wait_for_not_clicked.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Invalid data. Please enter the details correctly.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    wait_for_not_clicked.setText("Please wait while collecting data");
                }
                break;
            case R.id.test_button:
                break;
        }
    }
    public void initView() {

        // Assign buttons to variables
        progressBar_walk_var = (ProgressBar) findViewById(R.id.progressBarWalk);
        progressBar_walk_var.setProgress(trainWalkCount);
        progressBar_walk_var.setMax(20);
        progressBar_run_var = (ProgressBar) findViewById(R.id.progressBarRun);
        progressBar_run_var.setProgress(trainRunCount);
        progressBar_run_var.setMax(20);
        progressBar_jump_var = (ProgressBar) findViewById(R.id.progressBarJump);
        progressBar_jump_var.setProgress(trainJumpCount);
        progressBar_jump_var.setMax(20);

        test_button_var = findViewById(R.id.test_button);
        train_button_var= findViewById(R.id.train_button);

        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        test_button_var.setOnClickListener(this);
        train_button_var.setOnClickListener(this);

    }
}
