package com.example.chao.smoke;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao on 2017/4/7.
 * topicList为显示设备列表的list
 */
public class Data extends Application {
    public List<String> topicList = new ArrayList<>();
    public static String username;
    public static String password;
}
