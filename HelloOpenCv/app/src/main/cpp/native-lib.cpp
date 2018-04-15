#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "opencv2/imgcodecs.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/ml.hpp>
#include <iostream>
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
//=============
void function(float* arr1)
{
    // Data for visual representation
    int width = 512, height = 512;
    Mat image = Mat::zeros(height, width, CV_8UC3);

    // Set up training data
    const size_t N = 3; // Features
    const size_t M = 4; // Examples
    int Y_arr[M] = { 1, -1, -1, -1 };

    //// Initialize training data:
    //float X_arr[M][N] = // M x 3 => Num Training Examples x Spatial Dimensions of Each Sample
    //        { { 0, 256, 0 },
    //          { 256, 0, 0 },
    //          { 512, 0, 0 },
    //          { 512, 256, 0 } };

    //delinearize_2D(arr2d, flt1);
    float X_arr[4][3]; // M x 2 => Num Training Examples x Spatial Dimensions of Each Sample
    for (int i = 0; i < 4; ++i) // Depth of Feature Map
        for (int j = 0; j < 3; ++j) // Height of Feature Map
            X_arr[i][j] = arr1[i * 3 + j];

    // Copy arr into Mat
    Mat X(M, N, CV_32FC1, X_arr);
    Mat Y(M, 1, CV_32SC1, Y_arr);

    // Train the SVM:
    train(X, Y);
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
namespace josh {
    void svm(float* arrOut, float* arr1, int n) // modify to take variable size n
    {
        // Data for visual representation
        int width = 512, height = 512;
        Mat image = Mat::zeros(height, width, CV_8UC3);

        // Set up training data
        const size_t N = 3; // Features
        const size_t M = 4; // Examples
        int Y_arr[M] = { 1, -1, -1, -1 };

        //// Initialize training data:
        //float X_arr[M][N] = // M x 3 => Num Training Examples x Spatial Dimensions of Each Sample
        //        { { 0, 256, 0 },
        //          { 256, 0, 0 },
        //          { 512, 0, 0 },
        //          { 512, 256, 0 } };

        //delinearize_2D(arr2d, flt1);
        float X_arr[4][3]; // M x 2 => Num Training Examples x Spatial Dimensions of Each Sample
        for (int i = 0; i < 4; ++i) // Depth of Feature Map
            for (int j = 0; j < 3; ++j) // Height of Feature Map
                X_arr[i][j] = arr1[i * 3 + j];

        // Copy arr into Mat
        Mat X(M, N, CV_32FC1, X_arr);
        Mat Y(M, 1, CV_32SC1, Y_arr);

        // Train the SVM:
        train(X, Y);

        // Copy data into arrOut if you want to return data to Java
        arrOut[0] = 0;
    }
}
//=====================================
template <class T, size_t dim1, size_t dim2, size_t dim3>
void linearize_3D(T* arr_out, const T(&arr_in)[dim1][dim2][dim3])
{
    for (int i = 0; i < dim1; ++i) // Depth of Feature Map
        for (int j = 0; j < dim2; ++j) // Height of Feature Map
            for (int k = 0; k < dim3; ++k) // Width of Feature Map
                arr_out[((i * dim3 * dim2) + (j * dim3) + k)] = arr_in[i][j][k];
}
//=======================================================
template <class T, size_t dim1, size_t dim2, size_t dim3>
void delinearize_3D(T(&arr_out)[dim1][dim2][dim3], const T* arr_in)
{
    for (int i = 0; i < dim1; ++i) // Depth of Feature Map
        for (int j = 0; j < dim2; ++j) // Height of Feature Map
            for (int k = 0; k < dim3; ++k) // Width of Feature Map
                arr_out[i][j][k] = arr_in[((i * dim3 * dim2) + (j * dim3) + k)];
}
//=======================================================
template <class T, size_t dim1, size_t dim2>
void delinearize_2D(T(&arr_out)[dim1][dim2], const T* arr_in)
{
    for (int i = 0; i < dim1; ++i) // Depth of Feature Map
        for (int j = 0; j < dim2; ++j) // Height of Feature Map
            arr_out[i][j] = arr_in[i * dim2 + j];
}
//==========================
extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_josh_helloopencv_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
//==========================
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_josh_helloopencv_MainActivity_validate(JNIEnv *env, jobject instance,
                                                        jlong matAddrGr, jlong matAddrRgba) {
    //cv::Rect();
    cv::Mat testMat;
    std::string hello2 = "Hello from validate()";

    //// Call function with SVM stuff:
    //function();

    return env->NewStringUTF(hello2.c_str());
}
//==========================
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_josh_helloopencv_MainActivity_test(JNIEnv *env, jobject instance,
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
    function(flt1);

    env->ReleaseFloatArrayElements(fltarray1, flt1, 0);
    env->ReleaseFloatArrayElements(fltarray2, flt2, 0);
    env->SetFloatArrayRegion(result, 0, N, array1); // Range: [0,N]
    return result;
}
// ==========================
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_josh_helloopencv_MainActivity_svm(JNIEnv *env, jobject instance,
                                                   jfloatArray fltarray1) {
    const int N = 12;

    jfloatArray result;
    result = env->NewFloatArray(N);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // increase to len-4
    jfloat array1[N];
    jfloat* flt1 = env->GetFloatArrayElements( fltarray1,0);

    // Pass flt1 into the SVM function
    float vecRtrn[N];
    josh::svm(vecRtrn, flt1, N);

    // Copy data from C++ float array to the funky jfloatArray type - put in function to get out of sight
    for (int i = 0; i < N; i++) // Copy to output array
        array1[i] = vecRtrn[i];
    env->ReleaseFloatArrayElements(fltarray1, flt1, 0);
    env->SetFloatArrayRegion(result, 0, N, array1); // Range: [0,N]
    return result;
}