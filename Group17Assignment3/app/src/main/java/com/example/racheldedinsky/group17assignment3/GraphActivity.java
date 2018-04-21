package com.example.racheldedinsky.group17assignment3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by asmodi on 4/20/18.
 */

public class GraphActivity extends AppCompatActivity {

    private WebView visualGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);
        visualGraph = findViewById(R.id.plotlyGraph);
        visualGraph.setWebContentsDebuggingEnabled(true);

        visualGraph.setWebChromeClient(new WebChromeClient());

        // enable JS
        WebSettings webSettings = visualGraph.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getPath()
                + "/CSE535_ASSIGNMENT3/activityDB.db", null);

        Cursor walkData = db.rawQuery("SELECT * FROM Test where ActivityID like 'w%' limit 20", null);
        Cursor runData = db.rawQuery("SELECT * FROM Test where ActivityID like 'r%' limit 20", null);
        Cursor jumpData = db.rawQuery("SELECT * FROM Test where ActivityID like 'j%' limit 20", null);
        final float[][][] dataArray;
        dataArray = new float[60][3][50];
        int column = 0;
        int row = 0;
        while (walkData.moveToNext()) {
            column=0;
            for (int i = 1; i <= 150; i+=3) {
                dataArray[row][0][column] = walkData.getFloat(i);
                dataArray[row][1][column] = walkData.getFloat(i+1);
                dataArray[row][2][column] = walkData.getFloat(i+2);
                Log.d("array","row "+ row +" "+dataArray[row][0][column]+" "+dataArray[row][1][column]+" "+ dataArray[row][2][column]);
                column++;
            }
            row++;

        }
        while (runData.moveToNext()) {
            column=0;
            for (int i = 1; i <= 150; i+=3) {
                dataArray[row][0][column] = runData.getFloat(i);
                dataArray[row][1][column] = runData.getFloat(i+1);
                dataArray[row][2][column] = runData.getFloat(i+2);
                Log.d("array","row "+ row +" "+dataArray[row][0][column]+" "+dataArray[row][1][column]+" "+ dataArray[row][2][column]);
                column++;
            }
            row++;

        }
        while (jumpData.moveToNext()) {
            column=0;
            for (int i = 1; i <= 150; i+=3) {
                dataArray[row][0][column] = jumpData.getFloat(i);
                dataArray[row][1][column] = jumpData.getFloat(i+1);
                dataArray[row][2][column] = jumpData.getFloat(i+2);
                Log.d("array","row "+ row +" "+dataArray[row][0][column]+" "+dataArray[row][1][column]+" "+ dataArray[row][2][column]);
                column++;
            }
            row++;

        }
        //Log.d("array", dataArray[rowData.getCount()-1][0][49]+" "+dataArray[rowData.getCount()-1][1][49]);

        visualGraph.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.d("in js", "hello");
                JSONObject jsonObj = null;
                Gson gson = new Gson();
                String json = gson.toJson(dataArray);


                visualGraph.evaluateJavascript("plotgraph(" + json + ")", null);
            }
        });
        visualGraph.loadUrl("file:///android_asset/www/plotlyEX.html");

    }
}
