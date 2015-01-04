LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := sample
LOCAL_SRC_FILES := sample.cpp

include $(BUILD_SHARED_LIBRARY)
