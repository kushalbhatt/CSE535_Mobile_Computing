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

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //Create these global variables to be accesses between methods within the MainActivity class.
    private String horLabels[] = {"2700","2750","2800","2850","2900","2950","3000","3050","3100"," "};;
    private String verLabels[] = {" ","2000","1500","1000","500"};
    private GraphView ecg;
    private float values[]=new float[10];
    private RelativeLayout main_view;
    private Button run, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Initialize the view.
        initView();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Give the ECG line its values for the initial application View.
        for (int i = 0; i < 10; i++)
            values[i] = 0;
        //Change the application view.
        changeView();
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
        //When the run button is pressed, the following if statement runs.
        if(view.getId()==R.id.run)
        {
            Toast.makeText(this, "Running it!", Toast.LENGTH_SHORT).show();
            //Give the ECG line it's random values within the maximum and minimum constraints.
            Random random = new Random();
            for (int i = 0; i < 10; i++)
                values[i] = (random.nextFloat() * 10000) % 2500;
            //Change the view with the new values of the line.
            changeView();
        }
        //When the stop button is pressed, the following statement runs.
        else
        {
            //Remove the previous view.
            //main_view.removeView(ecg);
            main_view.removeAllViews();
            initView();
            Toast.makeText(this,"Stopping it!",Toast.LENGTH_SHORT).show();
            //Give the ECG line zero values to fulfill the GraphView input requirements.
            for (int i = 0; i < 10; i++)
                values[i] = 0;
            //Change the view with the new values of the line.
            changeView();
        }
    }

    //Initializes the blank graph view.
    public void initView()
    {
        setContentView(R.layout.activity_main);
        main_view=findViewById(R.id.main_content);
        run = findViewById(R.id.run);
        stop = findViewById(R.id.stop);
        run.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    public void changeView()
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
