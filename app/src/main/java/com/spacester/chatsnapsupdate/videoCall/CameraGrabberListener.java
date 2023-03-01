package com.spacester.chatsnapsupdate.videoCall;

public interface CameraGrabberListener {
    void onCameraInitialized();
    void onCameraError(String errorMsg);
}
