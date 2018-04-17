#include <jni.h>
#include <string>
#include <iostream>

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "opencv2/imgcodecs.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/ml.hpp>
using namespace cv;
using namespace cv::ml;
using std::cout;
//===========================
Ptr<SVM> svm = SVM::create();
//==============
void train(Mat X, Mat Y)
{
    // Train the SVM
    svm->setType(SVM::C_SVC);
    svm->setKernel(SVM::LINEAR);
    svm->setTermCriteria(TermCriteria(TermCriteria::MAX_ITER, 100, 1e-6));
    svm->train(X, ROW_SAMPLE, Y);
}
//=====================================
void svm_4x150(float* arrOut, float* arr1, float* arr2) // modify to take variable size n
{
    // Set up training data
    const size_t M = 4; // Examples
    const size_t N = 150; // Features

    // 1D -> 2D
    float X_arr[M][N];
    int Y_arr[M][1];
    for (int i = 0; i < M; ++i)
    {
        for (int j = 0; j < N; ++j)
        {
            X_arr[i][j] = arr1[i * N + j];
        }
        Y_arr[i][0] = static_cast<int>(arr2[i]);
    }

    // Copy arr into Mat
    Mat X(M, N, CV_32FC1, X_arr);
    Mat Y(M, 1, CV_32SC1, Y_arr);  // TO FIX THIS JUST PUT Y in a 2D array then pass it in here

    // Train the SVM:
    train(X, Y);

    // Create a set of N-D vectors to test the trained SVM with
    cv::Size size(N, 1);
    Mat mat1 = Mat::zeros(size, CV_32FC1);

    // Copy data into arrOut if you want to return data to Java
    arrOut[0] = 0;
}

//=========================================================
void mat_mult(float* arr3, float* arr1, float* arr2, int n)
{
    //for (int i = 0; i < n; i++)
    //    arr3[i] = arr1[i] + arr2[i];

    for (int i = 0; i < 2; i++) { // Only works for n = 4

        for (int j = 0; j < 2; j++) {
            float temp = 0.0f;
            for (int k = 0; k < 2; k++) {
                temp += arr1[i + k * 2] * arr2[j * 2 + k];
            }
            arr3[i * 2 + j] = temp;
        }
    }
}
//=====================================
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_racheldedinsky_group17assignment3_MainActivity_stringFromJNI(JNIEnv *env,
                                                                              jobject instance) {

    // TODO
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
//==========================
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_racheldedinsky_group17assignment3_MainActivity_test(JNIEnv *env, jobject instance,
                                                                     jfloatArray fltarray1,
                                                                     jfloatArray fltarray2) {
    const int N = 9;

    jfloatArray result;
    result = env->NewFloatArray(N);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // increase to len-4
    jfloat array1[N];
    jfloat* flt1 = env->GetFloatArrayElements( fltarray1,0);
    jfloat* flt2 = env->GetFloatArrayElements( fltarray2,0);

    // Perform matrix multiplication
    float vecRtrn[N];
    mat_mult(vecRtrn, flt1, flt2, N);
    for (int i = 0; i < N; i++) // Copy to output array
        array1[i] = vecRtrn[i];

    // Pass flt1 into the SVM function
    //function(flt1);

    env->ReleaseFloatArrayElements(fltarray1, flt1, 0);
    env->ReleaseFloatArrayElements(fltarray2, flt2, 0);
    env->SetFloatArrayRegion(result, 0, N, array1); // Range: [0,N]
    return result;
}
//========
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_racheldedinsky_group17assignment3_MainActivity_svm(JNIEnv *env, jobject instance,
                                                                    jfloatArray X_,
                                                                    jfloatArray Y_) {
    const int M = 4;
    const int N = M * 150;

    jfloatArray result;
    result = env->NewFloatArray(N);
    if (result == NULL) {
        return NULL; // out of memory error thrown
    }

    // increase to len-4
    jfloat array1[N];
    jfloat* flt1 = env->GetFloatArrayElements( X_, 0 );
    jfloat* flt2 = env->GetFloatArrayElements( Y_, 0 );

    // Pass flt1 into the SVM function
    float vecRtrn[N];
    svm_4x150(vecRtrn, flt1, flt2);

    // Copy data from C++ float array to the funky jfloatArray type - put in function to get out of sight
    for (int i = 0; i < N; i++) // Copy to output array
        array1[i] = vecRtrn[i];
    env->ReleaseFloatArrayElements( X_, flt1, 0 );
    env->ReleaseFloatArrayElements( Y_, flt2, 0);
    env->SetFloatArrayRegion(result, 0, N, array1); // Range: [0,N]
    return result;
}