package com.example.chao.smoke;

import org.litepal.crud.DataSupport;

/**
 * Created by feihu on 2017/8/9.
 * 设备的Java bean用于数据库操作
 */

public class Device extends DataSupport{
    private int id;
    private String topic;
    private int qos;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
