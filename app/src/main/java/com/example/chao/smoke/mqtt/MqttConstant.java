package com.example.chao.smoke.mqtt;

/**
 * Created by chao on 2017/4/12.
 */

public class MqttConstant {
    static final String ACTION_START = "start1";
    static final String ACTION_SUB = "sub";
    static final String ACTION_UNSUB = "unsub";
    static final String ACTION_LOGOUT = "logout";
    public static final String TOPICS = "设备号";
    public static final String QOSS="消息级别";
    public static final String ACTIONOFSUBBROADCAST = "com.zengchao.mqtt.sub";
    public static final String SubState = "substate";
    public static final int SUBSCRIBED = 9;
    public static final int SUBSCRIBING = 10;
    public static final String SUBSUCESS = "绑定设备成功";
    public static final String SUBERROR = "绑定设备失败";
    public static final int  UNSUBSUESS = 11;
    public static final int  UNSUBERROR = 12 ;
    public static final int  LOGOUTSUCESS = 13 ;
    public static final int  LOGOUTERROR = 14 ;
    public static final String LOGOUTSUCESSINFO = "注销成功";
    public static final String LOGOUTERRORINFO = "注销失败";
    public static final String UNSUBSUCESSINF = "解除绑定成功";
    public static final String UNSUBERRORINFO = "解除绑定失败";
}
