package com.example.pawfect;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
