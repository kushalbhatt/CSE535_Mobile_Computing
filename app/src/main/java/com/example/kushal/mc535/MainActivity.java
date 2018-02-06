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
import java.lang.*;
import android.content.*;
import android.os.Handler;
import java.util.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //Create these global variables to be accesses between methods within the MainActivity class.
    private int num_array_elems = 100;
    private String horLabels[] = {"2700","2750","2800","2850","2900","2950","3000","3050","3100"," "};;
    private String verLabels[] = {" ","2000","1500","1000","500","0"};
    private GraphView ecg;
    private float values[]=new float[num_array_elems];
    private RelativeLayout main_view;


    private Button run_button_var, stop_button_var;

    // Status flag for pausing
    private boolean pause_flag  = false;


    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

         //Initialize the view.
        initView();

        //why do we need floating button? --Kushal
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //Give the ECG line its values for the initial application View.
        for (int i = 0; i < num_array_elems; i++)
            values[i] = 0; // Initialize the value array to zeros

        //add Graphview to MainView.
        inflateGraphView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This button listener class will initiate actions when buttons are pressed.
    @Override
    public void onClick(View view)
    {
        // Use switch statement to determine which button was clicked
        switch(view.getId()) {
            case R.id.run_button:
                Toast.makeText(this, "Run button pressed", Toast.LENGTH_SHORT).show();
                pause_flag = false;
                break;
            case R.id.stop_button:
                Toast.makeText(this, "Stop button pressed", Toast.LENGTH_SHORT).show();
                pause_flag = true;
                break;
        }

        // Buggy part
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                running(pause_flag);
                handler.postDelayed(this, 750);
            }
        };
        handler.postDelayed(runnable, 750);

    }

    public void running(boolean pause_flag)
    {
        // Check to see if pause_flag has been set, if not, then draw graph
        if (pause_flag == false) {
            Random randVal = new Random();
            float step_size = 0.1f;
            float lower_x_bound = -1;
            float x = lower_x_bound;
            for (int i = 0; i < num_array_elems; ++i) {

                int number = randVal.nextInt(10);
                float noise = ((float) number) / 2.0f;

                x += step_size;
                values[i] = (float) Math.sin(x) + noise;
            }
        }
        else
        {
            //main_view.removeAllViews();
            //This is not good. Don't inflate views again and again unless there's no other solution available.
            //initView();
            //changeView();

            /***
             A possible alternative -- Kushal
             set values to null and then refresh the custom view
             so that the graph will be cleared
             ***/
            values = new float[num_array_elems];
        }

        ecg.setValues(values);
        ecg.invalidate();
    }
    //Initializes the blank graph view.
    public void initView()
    {
        setContentView(R.layout.activity_main);
        main_view=findViewById(R.id.main_content);

        // Assign buttons to variables
        run_button_var = (Button)findViewById(R.id.run_button);
        stop_button_var = (Button)findViewById(R.id.stop_button);

        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        run_button_var.setOnClickListener(this);
        stop_button_var.setOnClickListener(this);
    }

    public void inflateGraphView()
    {
        //Initialize GraphView.
        ecg = new GraphView(this,values,"Test ECG",horLabels,verLabels,GraphView.LINE);

        //Dynamically add Graphview to parent view.

        //First decide LayoutParameters.
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.rg1);
        layoutParams.addRule(RelativeLayout.ALIGN_START,R.id.sex);

        //Apply them to graph.
        ecg.setLayoutParams(layoutParams);

        //Add it to main_view.
        main_view.addView(ecg);
    }
}
