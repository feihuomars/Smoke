package com.example.chao.smoke;

/**
 * Created by chao on 2017/4/6.
 * Http回调接口
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
    void onBad(String a);
}
