package com.example.chao.smoke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.mqttService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chao on 2017/4/30.
 */

public class connectTab extends Fragment {
    private String mDeviceId;
    private EditText ehost;
    private EditText eport;//用户名和密码的文本框
    private EditText estate;
    private String host;
    private String port;

    private Button connect;//注册按钮

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ConnectedState:
                    Toast.makeText(getActivity(), Constant.STATEOFCONNECSUCESS, Toast.LENGTH_LONG).show();
                    estate.setText("已连接");
                    break;
                case Constant.ConnectingState:
                    Toast.makeText(getActivity(), Constant.STATEOFCONNECTERROR, Toast.LENGTH_LONG).show();
                    estate.setText("未连接");
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(Constant.STATEOFCONNECT, Constant.ConnectingState);
            Message message = new Message();
            message.what = state;
            mHandler.sendMessage(message);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect, container, false);
        mDeviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Constant.SharePreName, MODE_PRIVATE);
        ehost = (EditText) view.findViewById(R.id.host);
        estate = (EditText) view.findViewById(R.id.StateOfconnect);
        eport = (EditText) view.findViewById(R.id.port);
        connect = (Button) view.findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host = ehost.getText().toString();
                port = eport.getText().toString();
                Toast.makeText(getActivity(), "正在连接", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                        Constant.SharePreName, MODE_PRIVATE).edit();
                editor.putString(Constant.HOST, host);
                editor.putInt(Constant.PORT, Integer.parseInt(port));
                editor.putBoolean(Constant.HOSTANDPORTEXIT, true);
                editor.putString(Constant.DeviceID, mDeviceId);
                editor.apply();
                //在此启动服务
                mqttService.actionStart(getActivity().getApplicationContext());

            }
        });
        //mqttService.actionSub(getActivity().getApplicationContext(), "warning", 0);
        //mqttService.actionSub(getActivity().getApplicationContext(), "warning", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTIONOFBROADCASECON);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
