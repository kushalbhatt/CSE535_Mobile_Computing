package com.example.josh.helloopencv;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView textView = (TextView) findViewById(R.id.sample_text);
        textView.setText(stringFromJNI());

        if (!OpenCVLoader.initDebug()) {
            textView.setText(textView.getText() + "\n OpenCVLoader.initDebug() is NOT working.");
        } else {
            textView.setText(textView.getText() + "\n OpenCVLoader.initDebug() IS working.");
            textView.setText(textView.getText() + "\n" + validate(0L, 0L));
        }

        // Test the C-function:
        int n = 12;
        float[] array3 = new float[n];
        float[] array1 = new float[n];
        float[] array2 = new float[n];

        array1[0] = 0;      array1[1] = 255;    array1[2] = 0;
        array1[3] = 255;    array1[4] = 0;      array1[5] = 0;
        array1[6] = 512;    array1[7] = 0;      array1[8] = 0;
        array1[9] = 512;    array1[10] = 255;   array1[11] = 0;

        for (int i = 0; i < n; i++)
            array2[i] = array1[i];


        array3 = test(array1, array2);

        // =========================================================================================
        int M = 4, N = 3;
        float[] X_lin = new float[M * N]; // Data matrix with M rows and N cols
        int count = 0;
        for (int i = 0; i < M * N; i+=N) {
            count = i; //* N + j; // Count up sequentially
            float temp;
            if (count < M*N / 2) {// 1st half of rows
                X_lin[count+0]=0;   X_lin[count+1]=255; X_lin[count+2]=0; // upper right of xy-plane
            }
            else {
                X_lin[count+0]=255; X_lin[count+1]=0;   X_lin[count+2]=0; // lower left of xy-plane
            }
            //  z=0
            //     \  x=0   x=1  ...  x=N-1
            //      \----------------------
            // y=0  |_____|_____|...|______|
            // y=1  |_____|_____|...|______|
            //  .   |_____|_____|...|______|
            //  .   |_____|_____|...|______|
            //  .   |_____|_____|...|______|
            // y=M-1|_____|_____|...|______|
        }

        // Copy 1D array into 2D array:
        float[][] X_mat = new float[M][N];
        X_mat = one2twoD(X_lin, M, N);

        // Copy 2D array into 1D array:
        float[] X = new float[M * N];
        X = two2oneD(X_mat, M, N);

        // Pass data matrix to svm in C++
        float[] arrayTest = new float[M * N];
        arrayTest = svm(X);

        int temp0 = 0;

    } // End onCreate()
    //==============================================================================================
    public float[][] one2twoD(float[] arr, int rows, int cols) {
        float[][] mat = new float[rows][cols]; // Data matrix with M rows and N cols
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j){
                mat[i][j] = arr[i * cols + j]; // Count up in row-major fashion
            } // end for over j
        } // end for over i
        return mat;
    }
    public float[] two2oneD(float[][] mat, int rows, int cols) {
        float[] arr = new float[rows * cols]; // Data matrix with M rows and N cols
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j){
                arr[i * cols + j] = mat[i][j]; // Count up in row-major fashion
            } // end for over j
        } // end for over i
        return arr;
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String validate(long matAddrGr, long matAddrRgba);
    // Remember that you set the environment variable also
    public native float[] test(float[] arr1, float[] arr2);
    public native float[] svm(float[] arr);
}

