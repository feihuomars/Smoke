package com.example.chao.smoke;

import org.litepal.crud.DataSupport;

/**
 * Created by feihu on 2017/8/2.
 * 接收到的消息的java bean用于数据库操作
 */

public class Message extends DataSupport{
    private int id;
    private String message;
    private String time;
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
