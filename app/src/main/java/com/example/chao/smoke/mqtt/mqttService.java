package com.example.chao.smoke.mqtt;

/**
 * Created by chao on 2017/4/12.
 * mqtt相关的主要操作包括建立连接、订阅主题等功能
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

import com.example.chao.smoke.Constant;
import com.example.chao.smoke.Data;
import com.example.chao.smoke.MainActivity;
import com.example.chao.smoke.Message;
import com.example.chao.smoke.R;
import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttAdvancedCallback;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistenceException;

import java.util.HashMap;


public class mqttService extends Service {
    private HashMap<String, String> hashMap = new HashMap<>();
    private static String MQTT_HOST = "47.94.246.26";
    private static int MQTT_BROKER_PORT_NUM = 1883;
    private static boolean MQTT_CLEAN_START = true;
    private static short MQTT_KEEP_ALIVE = 10;
    public static String MQTT_CLIENT_ID;
    private boolean mConected = false;
    private SharedPreferences mPrefs;
    private MqttConection mqttConection;
    int[] qoss = new int[1];
    private Object conLock = new Object();
    String[] topics = new String[1];
    private boolean connLockNotified = false;
    private Object subLock = new Object();
    private boolean subLockNotified = false;
    private NotificationManager notificationManager;

    //以下四个静态方法供外部调用
    //断开链接
    public static void actionLogout(Context context) {
        Intent i = new Intent(context, mqttService.class);
        i.setAction(MqttConstant.ACTION_LOGOUT);
        context.startService(i);
    }
    //建立链接
    public static void actionStart(Context context) {
        Intent i = new Intent(context, mqttService.class);
        i.setAction(MqttConstant.ACTION_START);
        context.startService(i);
    }
    //订阅主题包括topic和qos
    public static void actionSub(Context context, String topic, int qos) {
        Intent i = new Intent(context, mqttService.class);
        i.putExtra(MqttConstant.TOPICS, topic);
        i.putExtra(MqttConstant.QOSS, qos);
        i.setAction(MqttConstant.ACTION_SUB);
        context.startService(i);
        Log.i("mqttService", "actionsub started");
    }
    //取消订阅
    public static void actionUnsub(Context context, String topic, int qos) {
        Intent i = new Intent(context, mqttService.class);
        i.putExtra(MqttConstant.TOPICS, topic);
        i.putExtra(MqttConstant.QOSS, qos);
        i.setAction(MqttConstant.ACTION_UNSUB);
        context.startService(i);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //初始化服务器地址、端口、客户id等信息
    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = getSharedPreferences(Constant.SharePreName, MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        topics[0] = null;
        if (mPrefs.getBoolean(Constant.HOSTANDPORTEXIT, false)) {
            MQTT_BROKER_PORT_NUM = mPrefs.getInt(Constant.PORT, 1883);
            MQTT_HOST = mPrefs.getString(Constant.HOST, "47.94.246.26");
            MQTT_CLIENT_ID = mPrefs.getString(Constant.DeviceID, "unnamed");
        } else {
            Log.i("intial ", "no initial use the original value");
        }
    }
    //根据intent中包含的action信息进入不同的函数处理
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals(MqttConstant.ACTION_START)) {
                    start();
                    /*Message message = new Message();
                    message.what = 1;*/
                } else if (intent.getAction().equals(MqttConstant.ACTION_SUB)) {
                    if (intent.getStringExtra(MqttConstant.TOPICS) != null) {

                        topics[0] = intent.getStringExtra(MqttConstant.TOPICS);
                        qoss[0] = intent.getIntExtra(MqttConstant.QOSS, 1);

                        subscrible(topics, qoss);
                    } else {
                        Log.e("error", "topic and qos not found ");
                    }

                } else if (intent.getAction().equals(MqttConstant.ACTION_UNSUB)) {
                    if (intent.getStringExtra(MqttConstant.TOPICS) != null) {
                        topics[0] = intent.getStringExtra(MqttConstant.TOPICS);
                        qoss[0] = intent.getIntExtra(MqttConstant.QOSS, 1);
                        unsubscrible(topics, qoss);
                    }

                } else if (intent.getAction().equals(MqttConstant.ACTION_LOGOUT)) {
                    Intent intent2 = new Intent(MqttConstant.ACTIONOFSUBBROADCAST);
                    if (disconnectClient()) {
                        destoryClient();
                        intent2.putExtra(MqttConstant.SubState, MqttConstant.LOGOUTSUCESS);
                    } else {
                        intent2.putExtra(MqttConstant.SubState, MqttConstant.LOGOUTERROR);
                    }
                    sendBroadcast(intent2);

                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean disconnectClient() {
        synchronized (conLock) {
            connLockNotified = true;
            conLock.notify();
        }
        try {
            if (mqttConection != null) {
                mqttConection.mqttClient.disconnect();
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
            return false;
        }
        mConected = false;
        return true;
    }

    public boolean destoryClient() {
        if (mqttConection != null) {
            mqttConection.mqttClient.terminate();
        }
        mqttConection = null;
        return true;
    }
    //订阅方法
    private void subscrible(String[] topics, int[] qoss) {
        boolean ret = false;
        try {
//            synchronized (subLock) {
//                Log.e("error", "startsub");
//                //此处调用mqtt包中的方法订阅
//                mqttConection.mqttClient.subscribe(topics, qoss);
//                Log.i("mqttService", "topic:" + topics[0]);
//                Log.i("mqttService", "QOS:" + qoss[0]);
//                while (!subLockNotified) {
//                    subLock.wait();
//                }
//                Log.e("error", "startsubsucess");
//                subLockNotified = false;
//            }

            Log.e("error", "startsub");
            mqttConection.mqttClient.subscribe(topics, qoss);
            Log.i("mqttService", "topic:" + topics[0]);
            Log.i("mqttService", "QOS:" + qoss[0]);
            Log.e("error", "startsubsucess");


            ret = true;
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        } finally {
            Intent intent2 = new Intent(MqttConstant.ACTIONOFSUBBROADCAST);
            if (ret) {
                intent2.putExtra(MqttConstant.SubState, MqttConstant.SUBSCRIBED);
                Log.i("hello", "添加设备" + topics[0]);
                if (getData().topicList.contains(topics[0])) {
                    //do nothing
                } else {
                    getData().topicList.add(topics[0]);
                }
                sendBroadcast(intent2);
            } else {
                intent2.putExtra(MqttConstant.SubState, MqttConstant.SUBSCRIBING);
                sendBroadcast(intent2);
            }
        }
    }

    public void unsubscrible(String[] topics, int[] qoss) {
        boolean u = false;
        try {
            synchronized (subLock) {
                mqttConection.mqttClient.unsubscribe(topics);
                while (!subLockNotified) {
                    subLock.wait();
                }
                subLockNotified = false;
            }
            u = true;
        } catch (Exception e) {
            u = false;
            e.printStackTrace();
        } finally {
            Intent intent2 = new Intent(MqttConstant.ACTIONOFSUBBROADCAST);
            if (u) {
                Log.i("hello", "删除设备" + topics[0]);
                getData().topicList.remove(topics[0]);
                intent2.putExtra(MqttConstant.SubState, MqttConstant.UNSUBSUESS);
            } else {
                intent2.putExtra(MqttConstant.SubState, MqttConstant.UNSUBERROR);
            }
            sendBroadcast(intent2);
        }
    }

    synchronized private void start() {
        if (mConected == true) {
            return;
        } else {
            try {
                mqttConection = new MqttConection();
                mConected = true;
            } catch (MqttException e) {
                mConected = false;

                e.printStackTrace();
            } finally {
                Intent intent1 = new Intent(Constant.ACTIONOFBROADCASECON);
                if (mConected) {
                    intent1.putExtra(Constant.STATEOFCONNECT, Constant.ConnectedState);

                } else {
                    intent1.putExtra(Constant.STATEOFCONNECT, Constant.ConnectingState);
                }
                sendBroadcast(intent1);
            }
        }
    }

    private class MqttConection implements MqttAdvancedCallback {
        public IMqttClient mqttClient = null;

        public MqttConection() throws MqttException {
            String hostAddress = MqttClient.TCP_ID + MQTT_HOST +
                    ":" + MQTT_BROKER_PORT_NUM;
            mqttClient = MqttClient.createMqttClient(hostAddress, null);
            mqttClient.connect(MQTT_CLIENT_ID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);
            mqttClient.registerAdvancedHandler(this);
        }

        @Override
        public void published(int i) {

        }

        @Override
        public void subscribed(int i, byte[] bytes) {
            synchronized (subLock) {
                subLockNotified = true;
                subLock.notify();
                Log.e("error", "starteeeeeeeeeeee");
            }

        }

        @Override
        public void unsubscribed(int i) {
            synchronized (subLock) {
                subLockNotified = true;
                subLock.notify();
            }
        }

        @Override
        public void connectionLost() throws Exception {
            boolean reconnected = false;
            synchronized (conLock) {
                while (!connLockNotified) {
                    try {
                        mqttClient.connect(MQTT_CLIENT_ID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);
                        connLockNotified = true;
                    } catch (MqttException mqe) {
                        Log.e("error", "reconnect error");
                    }

                }
                connLockNotified = false;

            }
           /* if ( reconnected )
            {
                try
                {
                    mqttClient.subscribe(topics,qoss);
                }
                catch (MqttException e)
                {
                    disconnectClient();
                    destoryClient();
                    throw e ;
                }
            }*/

        }
        //消息到达后的处理方法
        @Override
        public void publishArrived(String s, byte[] bytes, int i, boolean b) throws Exception {
            Log.i("message", s);            //topic
            String warning = new String(bytes); //payload

            //获取系统时间
            Time t = new Time();
            t.setToNow();
            String time = t.year + "-" + (t.month + 1) + "-" + t.monthDay + " " + t.hour + ":" + t.minute + ":" + t.second;
            Log.i("mqttServer", time);

            //Connector.getDatabase();
            //DataSupport.deleteAll(Message.class);

            Message message = new Message();
            message.setId(1);
            message.setMessage(warning);
            message.setTime(time);
            message.setTopic(s);
            message.save();

//            List<Message> messages = DataSupport.findAll(Message.class);
//            for (Message messagel : messages) {
//                Log.i("mqttService", "--------------------------");
//                Log.i("mqttService", "id:" + messagel.getId());
//                Log.i("mqttService", messagel.getMessage());
//                Log.i("ShowMessageActivity", messagel.getTime());
//                Log.i("mqttService", messagel.getTopic());
//                Log.i("mqttService", "--------------------------");
//            }

//            Message messagel = DataSupport.findLast(Message.class);
//            Log.i("mqttService", "id:" + messagel.getId());
//            Log.i("mqttService", messagel.getMessage());
//            Log.i("ShowMessageActivity", messagel.getTime());



            getData().topicList.add(warning);
            Intent intent = new Intent(mqttService.this, MainActivity.class);
            //intent.putExtra("message_test", "hello message_test");
            PendingIntent pi = PendingIntent.getActivity(mqttService.this, 0, intent, 0);
            //对通知的样式进行设置
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.icon)
                    .setVibrate(new long[]{0, 1000, 1000, 1000})
                    //.setLights(Color.GREEN, 1000, 1000)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setContentTitle(s)
                    .setContentText(warning);
            notificationManager.notify(i, builder.build());
        }
    }

    public Data getData() {
        return ((Data) getApplicationContext());
    }

}
