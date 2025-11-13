cmake_minimum_required(VERSION 3.22.1)
project("flamassessment")

# --- Simplified Linkage ---
find_package(OpenCV REQUIRED)

if (OpenCV_FOUND)
    message(STATUS "OpenCV successfully found and linked.")
else()
    message(FATAL_ERROR "OpenCV not found. Check Gradle dependency setup.")
endif()

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        opencv_java4)
