package com.example.chao.smoke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.MqttConstant;
import com.example.chao.smoke.mqtt.mqttService;

import org.litepal.crud.DataSupport;

import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Created by chao on 2017/5/1.
 * 添加设备订阅主题
 */

public class addDevicedTab extends Fragment {
    private EditText etopic;
    private EditText eqos;//
    private String topic;
    private int qos;
    private Button unsub;
    private Button sub;
    private android.os.Handler Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MqttConstant.SUBSCRIBED:
                    Toast.makeText(getActivity(), MqttConstant.SUBSUCESS, Toast.LENGTH_LONG).show();
                    break;
                case MqttConstant.SUBSCRIBING:
                    Toast.makeText(getActivity(), MqttConstant.SUBERROR, Toast.LENGTH_LONG).show();
                    break;
                case MqttConstant.UNSUBSUESS:
                    Toast.makeText(getActivity(), MqttConstant.UNSUBSUCESSINF, Toast.LENGTH_LONG).show();
                    break;
                case MqttConstant.UNSUBERROR:
                    Toast.makeText(getActivity(), MqttConstant.UNSUBERRORINFO, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    //接收mqttService的广播消息，具体处理由handler处理
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(MqttConstant.SubState, MqttConstant.SUBSCRIBING);
            Message message = new Message();
            message.what = state;
            Handler.sendMessage(message);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_layout, container, false);
        etopic = (EditText) view.findViewById(R.id.topic);
        eqos = (EditText) view.findViewById(R.id.qos);
        sub = (Button) view.findViewById(R.id.subscrible);
        unsub = (Button) view.findViewById(R.id.unsub);
        //mqttService.actionSub(getActivity().getApplicationContext(), "warning", 0);

        List<Device> devices = DataSupport.findAll(Device.class);
        for (Device device1 : devices) {
            Log.i("addDevicedTab", "Topic:" + device1.getTopic());
            Log.i("addDevicedTab", "QOS: " + device1.getQos());
            mqttService.actionSub(getActivity().getApplicationContext(), device1.getTopic(), device1.getQos());
        }


        //mqttService.actionSub(getActivity().getApplicationContext(), "warning", 0);
        //mqttService.actionSub(getActivity().getApplicationContext(), "warnings", 0);

        //相应按钮点击事件
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = etopic.getText().toString();
                qos = parseInt(eqos.getText().toString());
                if (getData().topicList.contains(topic)) {
                    Toast.makeText(getActivity(), "请勿重复绑定", Toast.LENGTH_SHORT).show();
                } else {
                    mqttService.actionSub(getActivity().getApplicationContext(), topic, qos);
                    //mqttService.actionSub(getActivity().getApplicationContext(), "warning", 0);
                    Device device = new Device();
                    device.setId(1);
                    device.setTopic(topic);
                    device.setQos(qos);
                    device.save();


                }
            }
        });

        unsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = etopic.getText().toString();
                qos = parseInt(eqos.getText().toString());
                if (getData().topicList.contains(topic)) {
                    mqttService.actionUnsub(getActivity().getApplicationContext(), topic, qos);
                } else {
                    Toast.makeText(getActivity(), "请勿重复取消绑定", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction((MqttConstant.ACTIONOFSUBBROADCAST));
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public Data getData() {
        return ((Data) getActivity().getApplicationContext());
    }
}
