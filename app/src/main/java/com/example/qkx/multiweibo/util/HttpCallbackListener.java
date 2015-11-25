package com.example.qkx.multiweibo.util;

/**
 * Created by qkx on 2015/10/1.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
