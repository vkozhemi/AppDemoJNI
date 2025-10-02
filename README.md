# AppDemo – Android Image Filters

## Overview
AppDemo is Android 14 demo application demonstrates native image processing with JNI and C++ and modern Android best practices.  
It allows to pick image (from assets or storage), apply filters such as invert and box blur and save results into Gallery.

## Project Structure
app/ Main app (UI, Fragments, Activities, ViewModel, Repository)

mylibrary/ Native filters (JNI, C++)
 - jni/ JNI bridge
 - filters/ Core image filters (C++20)
 - CMakeLists.txt

## Getting Started
Clone the repo:
   https://github.com/vkozhemi/AppDemoJNI.git

Build and run on AOSP 14 emulator or device.

Example Usage
Pick an image - Apply invert and blur - Save to gallery - View in Photos.


