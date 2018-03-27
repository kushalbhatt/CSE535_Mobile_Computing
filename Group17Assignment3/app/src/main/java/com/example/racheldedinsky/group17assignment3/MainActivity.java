package com.example.racheldedinsky.group17assignment3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button train_button_var, test_button_var;
    int trainWalkCount, trainRunCount, trainJumpCount;
    String activity=null;
    RadioButton r;
    ProgressBar progressBar_walk_var, progressBar_run_var, progressBar_jump_var;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                int checked = ((RadioGroup)findViewById(R.id.rg1)).getCheckedRadioButtonId();
                View radioButton = findViewById(checked);
                int idx = ((RadioGroup) findViewById(R.id.rg1)).indexOfChild(radioButton);
                //Toast.makeText(getApplicationContext(), idx+"", Toast.LENGTH_LONG).show();
                if(idx==0) {
                    activity = "Walk";
                }
                else if(idx==1){
                    activity="Run";
                }
                else
                {
                    activity="Jump";
                }
                try
                {
                    if(!activity.isEmpty())
                    {
                        //Toast.makeText(getApplicationContext(), activity, Toast.LENGTH_LONG).show();
                        if(activity == "Walk")
                        {
                            trainWalkCount++;
                            progressBar_walk_var.setProgress(0);
                            progressBar_walk_var.setProgress(trainWalkCount);
                            Toast.makeText(getApplicationContext(), trainWalkCount+"", Toast.LENGTH_LONG).show();
                        }
                        else if(activity == "Run")
                        {
                            trainRunCount++;
                            progressBar_run_var.setProgress(0);
                            progressBar_run_var.setProgress(trainRunCount);
                            Toast.makeText(getApplicationContext(), trainRunCount+"", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            trainJumpCount++;
                            progressBar_jump_var.setProgress(0);
                            progressBar_jump_var.setProgress(trainJumpCount);
                            Toast.makeText(getApplicationContext(), trainJumpCount+"", Toast.LENGTH_LONG).show();
                        }
                    }

                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Invalid data. Please enter the details correctly.",Toast.LENGTH_SHORT).show();
                }
                activity="";
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
