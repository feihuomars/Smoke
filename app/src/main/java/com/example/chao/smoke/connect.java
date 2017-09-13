package com.example.chao.smoke;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.mqttService;

/**
 * Created by chao on 2017/4/11.
 * 该类已经被废弃
 */

public class connect extends Activity {
    private String mDeviceId;
    private EditText ehost;
    private EditText eport;//用户名和密码的文本框
    private String host;
    private String port;

    private Button connect;//注册按钮

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ConnectedState:
                    Toast.makeText(connect.this, Constant.STATEOFCONNECSUCESS, Toast.LENGTH_LONG).show();
                    unregisterReceiver(receiver);
                    Intent intent = new Intent(com.example.chao.smoke.connect.this, sub.class);
                    startActivity(intent);
                    break;
                case Constant.ConnectingState:
                    Toast.makeText(connect.this, Constant.STATEOFCONNECTERROR, Toast.LENGTH_LONG).show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);
        mDeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.SharePreName, MODE_PRIVATE);
        ehost = (EditText) findViewById(R.id.host);
        eport = (EditText) findViewById(R.id.port);
        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host = ehost.getText().toString();
                port = eport.getText().toString();
                Toast.makeText(connect.this, "正在连接", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = getSharedPreferences(
                        Constant.SharePreName, MODE_PRIVATE).edit();
                editor.putString(Constant.HOST, host);
                editor.putInt(Constant.PORT, Integer.parseInt(port));
                editor.putBoolean(Constant.HOSTANDPORTEXIT, true);
                editor.putString(Constant.DeviceID, mDeviceId);
                editor.commit();
                mqttService.actionStart(getApplicationContext());

            }
        });
    }

    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTIONOFBROADCASECON);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
