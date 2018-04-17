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
void svm_4x3(float* arrOut, float* arr1, float* arr2) // modify to take variable size n
{
    // Set up training data
    const size_t M = 4; // Examples
    const size_t N = 3; // Features

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

    auto debug1_y = Y.at<int>(0, 0);
    auto debug2_y = Y.at<int>(1, 0);
    auto debug3_y = Y.at<int>(2, 0);
    auto debug4_y = Y.at<int>(3, 0);

    auto debug1_x00 = X.at<float>(0, 0);
    auto debug2_x01 = X.at<float>(0, 1);
    auto debug3_x02 = X.at<float>(0, 2);

    auto debug1_x10 = X.at<float>(1, 0);
    auto debug2_x11 = X.at<float>(1, 1);
    auto debug3_x12 = X.at<float>(1, 2);

    auto debug1_x20 = X.at<float>(2, 0);
    auto debug2_x21 = X.at<float>(2, 1);
    auto debug3_x22 = X.at<float>(2, 2);

    auto debug1_x30 = X.at<float>(3, 0);
    auto debug2_x31 = X.at<float>(3, 1);
    auto debug3_x32 = X.at<float>(3, 2);

    // Train the SVM:
    train(X, Y);

    // Create a set of N-D vectors to test the trained SVM with
    cv::Size size(N, 1);
    Mat mat1 = Mat::zeros(size, CV_32FC1);


    int tempa = 0;
    int tempb = 0;
    int tempc = 0;
    int tempd = 0;

    // (0, 0) -> -1
    auto predict1 = svm->predict(mat1);


    // (0, 10) -> -1
    mat1.at<float>(0, 0) = 0;     mat1.at<float>(0, 1) = 10;
    auto predict2 = svm->predict(mat1);

    // (10, 0) -> -1
    mat1.at<float>(0, 0) = 10;     mat1.at<float>(0, 1) = 0;
    auto predict3 = svm->predict(mat1);

    // (0, 512) - > +1
    mat1.at<float>(0, 0) = 0;     mat1.at<float>(0, 1) = 512;
    auto predict4 = svm->predict(mat1);

    // (511, 0) -> -1
    mat1.at<float>(0, 0) = 512;     mat1.at<float>(0, 1) = 0;
    auto predict5 = svm->predict(mat1);

    // (512, 512) -> +1
    mat1.at<float>(0, 0) = 512;     mat1.at<float>(0, 1) = 512;
    auto predict6 = svm->predict(mat1);

    // (0, 256) ->  +1
    mat1.at<float>(0, 0) = 0;     mat1.at<float>(0, 1) = 256;
    auto predict7 = svm->predict(mat1);

    // (256, 0) -> -1
    mat1.at<float>(0, 0) = 256;     mat1.at<float>(0, 1) = 0;
    auto predict8 = svm->predict(mat1);

    // (512, 256) -> +1
    mat1.at<float>(0, 0) = 512;     mat1.at<float>(0, 1) = 256;
    auto predict9 = svm->predict(mat1);


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
    svm_4x3(vecRtrn, flt1, flt2);

    // Copy data from C++ float array to the funky jfloatArray type - put in function to get out of sight
    for (int i = 0; i < N; i++) // Copy to output array
        array1[i] = vecRtrn[i];
    env->ReleaseFloatArrayElements( X_, flt1, 0 );
    env->ReleaseFloatArrayElements( Y_, flt2, 0);
    env->SetFloatArrayRegion(result, 0, N, array1); // Range: [0,N]
    return result;
}