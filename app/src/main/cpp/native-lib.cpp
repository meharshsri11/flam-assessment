#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define TAG "V_NATIVE"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_flamassessment_ImageProcessor_Companion_processFrameNative(
        JNIEnv* env,
        jobject /* this */,
        jlong frameBufferAddress,
        jint width,
        jint height) {

    if (frameBufferAddress == 0) { return; }

    cv::Mat& mat = *(cv::Mat*)frameBufferAddress;

    // --- Core Processing Logic: Canny Edge Detection ---
    if (mat.channels() > 1) {
        cv::cvtColor(mat, mat, cv::COLOR_RGBA2GRAY);
    }
    cv::GaussianBlur(mat, mat, cv::Size(5, 5), 0, 0);
    cv::Canny(mat, mat, 100, 200);

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Frame processed successfully in C++.");
}
