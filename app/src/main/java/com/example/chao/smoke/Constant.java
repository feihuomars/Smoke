package com.example.chao.smoke;

/**
 * Created by chao on 2016/12/28.
 * 定义了一些消息常量
 */

public final  class Constant
{
     static final int Phone_Number=11;
     static final int Password_Min_Number=6;
     static final String IsMemorized = "isMemorized";
     static final String PasswordFileName = "UsernameAndPasswordFileName";
     static final String UsernameKey = "userName";
     static final String PasswordKey ="passWord";
     static final int  LOGINSUCESS = 1;
     static final String   LOGINERROR = "登陆失败" ;
     static final int  LOGINERRORint = 2 ;
     //static final String  HTTPADDRESS = "http://192.168.137.1:8000";
     //static final String  HTTPADDRESS = "http://10.59.13.199:8080/cloudserver/servlet/Test";
     static final String  HTTPADDRESS = "http://47.94.246.26:8000";
     static final int REGISTERSUCCESS =3;
     static final int REGISTERERROR =4;
     static final int REGISTERBAD = 5;
     public static final String  SharePreName = "data";
     public static final String  HOST ="host";
     public static final String  PORT ="port";
     public static final String  HOSTANDPORTEXIT ="IsExit";
     public static final int ConnectedState = 6;
     public static final String STATEOFCONNECT = "state";
     public static final int ConnectingState = 7 ;
     public static final String STATEOFCONNECTERROR = "连接失败";
     public static final String STATEOFCONNECSUCESS = " 连接成功";
     public static final String ACTIONOFBROADCASECON = "com.zengchao.mqtt.connect";
     public static final String DeviceID = "deviceid";
     public static final int CHANGES = 8;
     public static final int CHANGEE = 17;
     public static final int ALTERERROR = 18;
}
