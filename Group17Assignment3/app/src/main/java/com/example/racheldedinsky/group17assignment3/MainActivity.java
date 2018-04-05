package com.example.racheldedinsky.group17assignment3;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ProgressBar;
    /**
     * Created by RachelDedinsky on 4/4/18.
     */

    public class MainActivity extends Activity implements View.OnClickListener{
        Button train, test;
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            //Initialize the app view
            initView();
        }
        public void onClick(View view) {
            //switch statement based on which button
            switch (view.getId()) {
                case R.id.train_button:
                    startTrainActivity();
                    //setContentView(R.layout.activity_main);
                    break;
                case R.id.test_button:
                    //startTestActivity();
                    //setContentView(R.layout.test);
                    break;
            }
        }
        public void initView() {

            test = findViewById(R.id.test_button);
            train= findViewById(R.id.train_button);

            // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
            test.setOnClickListener(this);
            train.setOnClickListener(this);

        }
        public void startTrainActivity() {
            Intent someActivity = new Intent(MainActivity.this, trainActivity.class);
            startActivity(someActivity);
        }
        /*
        public void startTestActivity() {
            Intent someActivity = new Intent(MainActivity.this, testActivity.class);
            startActivity(someActivity);
        }*/
}
