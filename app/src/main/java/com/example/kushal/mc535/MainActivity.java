package com.example.kushal.mc535;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        RelativeLayout main_view = findViewById(R.id.main_content);

        Button run = findViewById(R.id.run);
        Button stop = findViewById(R.id.stop);
        run.setOnClickListener(this);
        stop.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Random random = new Random();
        float values[] = new float[10];
        for(int i=0;i<10;i++)
            values[i]= (random.nextFloat()*10000) % 2500;
        String horLabels[] = {"2700","2750","2800","2850","2900","2950","3000","3050","3100"};
        String verLabels[] = {"500","1000","1500","2000"};

        //initialize GraphView
        GraphView ecg = new GraphView(this,values,"Test ECG",horLabels,verLabels,GraphView.LINE);

        //now dynamically add Graphview to parent view

        //first decide layoutparameters
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.rg1);
        layoutParams.addRule(RelativeLayout.ALIGN_START,R.id.sex);

        //apply them to graph
        ecg.setLayoutParams(layoutParams);

        //add it to main view
        main_view.addView(ecg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.run:
            Toast.makeText(this,"Running it!",Toast.LENGTH_SHORT).show();break;
            case R.id.stop:
            Toast.makeText(this,"Stopping it!",Toast.LENGTH_SHORT).show();break;
            default:
                Toast.makeText(this,"Invalid Button even!",Toast.LENGTH_SHORT).show();
        }
    }
}
