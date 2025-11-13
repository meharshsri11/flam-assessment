🧪 Android + OpenCV (C++) Camera Processing + Web Viewer
Flam RnD Intern – Technical Assessment

This project implements real-time camera frame processing on Android using:

CameraX (camera feed)

OpenCV in C++ (JNI)

Native Canny Edge Detection

Modular project structure (/jni, /gl, /web)

A minimal TypeScript + HTML web viewer

✅ 1. Features Implemented
📱 Android App

Camera feed using CameraX ImageAnalysis

Frame conversion: Image → cv::Mat

JNI bridge to native C++ function

Canny Edge Detection applied in C++

Clean modular structure:

app/
jni/
gl/
web/


All processing logs visible in Logcat
("Frame processed successfully in C++")

🌐 Web Viewer (TypeScript)

Simple TypeScript project with:

Static processed frame preview

FPS + resolution display

DOM manipulation with TS

Files included:

index.html

main.ts

main.js

sample_edge_frame.png

🖼️ 2. Screenshots / GIF

(Add your images here)

Example:

/screenshots/
    camera_preview.png
    console_logs.png
    web_viewer.png

⚙️ 3. Setup Instructions
Android Requirements

Android Studio Flamingo or later

NDK installed

Tools → SDK Manager → SDK Tools → NDK (Side by side)


OpenCV Maven repo added in settings.gradle.kts:

maven { url = uri("https://repo.opencv.org/maven") }

Dependencies (app/build.gradle.kts)
implementation("org.opencv:opencv-android:4.8.0")
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")

CMake Linking
find_package(OpenCV REQUIRED)
target_link_libraries(${CMAKE_PROJECT_NAME}
    android
    log
    opencv_java4)

Run

Connect device

Build → Run

Grant camera permission

Logs show processed frames

🧩 4. Architecture Explanation
📌 Frame Flow
CameraX → ImageAnalysis → JPEG/YUV frame → toMat() →
JNI → C++ → OpenCV Canny Edge Detection →
processed Mat returned/logged

📌 JNI Integration

Kotlin calls:

processFrameNative(mat.nativeObjAddr, width, height)


C++ receives and modifies the cv::Mat directly.

📌 TypeScript Web Viewer

Static HTML page

Basic DOM updates

Displays sample processed frame

✨ Notes

OpenGL ES renderer was not fully implemented due to time constraints.

All core pipelines (Camera → OpenCV → JNI → Web) are complete.

📦 Submission

Full project with complete commit history is available in this GitHub repo.
