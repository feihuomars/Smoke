package com.example.chao.smoke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.MqttConstant;
import com.example.chao.smoke.mqtt.mqttService;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Created by chao on 2017/4/13.
 * 该类已经被废弃
 */

public class sub extends Activity {
    private ImageView setting;
    private EditText etopic;
    private EditText eqos;//
    private String topic;
    private int qos;
    private Button unsub;
    private Button sub;
    private Handler Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MqttConstant.SUBSCRIBED:
                    Log.e("hello", "suesess");

                    Toast.makeText(sub.this, MqttConstant.SUBSUCESS, Toast.LENGTH_LONG).show();
                    break;
                case MqttConstant.SUBSCRIBING:
                    Toast.makeText(sub.this, MqttConstant.SUBERROR, Toast.LENGTH_LONG).show();

                    break;
                case MqttConstant.UNSUBSUESS:
                    Toast.makeText(sub.this, MqttConstant.UNSUBSUCESSINF, Toast.LENGTH_LONG).show();

                    break;
                case MqttConstant.UNSUBERROR:
                    Toast.makeText(sub.this, MqttConstant.UNSUBERRORINFO, Toast.LENGTH_LONG).show();
                    break;
                case MqttConstant.LOGOUTSUCESS:
                    Toast.makeText(sub.this, MqttConstant.LOGOUTSUCESSINFO, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(sub.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case Constant.CHANGES:
                    Toast.makeText(sub.this, "change sucess", Toast.LENGTH_LONG).show();
                    mqttService.actionLogout(getApplicationContext());
                    break;
                case Constant.CHANGEE:
                    Toast.makeText(sub.this, "change error", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("message", "hekki");
            int state = intent.getIntExtra(MqttConstant.SubState, MqttConstant.SUBSCRIBING);
            Log.i("message", "hekki");
            Message message = new Message();
            message.what = state;
            Handler.sendMessage(message);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_layout);
        etopic = (EditText) findViewById(R.id.topic);
        eqos = (EditText) findViewById(R.id.qos);
        sub = (Button) findViewById(R.id.subscrible);
        unsub = (Button) findViewById(R.id.unsub);
        setting = (ImageView) findViewById(R.id.seting);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = etopic.getText().toString();
                qos = parseInt(eqos.getText().toString());
                mqttService.actionSub(getApplicationContext(), topic, qos);
            }
        });
        unsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = etopic.getText().toString();
                qos = parseInt(eqos.getText().toString());
                mqttService.actionUnsub(getApplicationContext(), topic, qos);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ChangePassword:
                LayoutInflater li = LayoutInflater.from(this);
                View alter = li.inflate(R.layout.alter, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(alter);
                final EditText userInput = (EditText) alter.findViewById(R.id.editTextDialogUserInput);
                builder.setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = userInput.getText().toString();

                                if (password != null) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("job", "change");
                                    map.put("username", Data.username);
                                    map.put("password", Data.password);
                                    map.put("newpassword", password);
                                    Http.sendHttpRequest(Constant.HTTPADDRESS, map, new HttpCallbackListener() {
                                        @Override
                                        public void onFinish(String response) {
                                            Message message = new Message();
                                            message.what = Constant.CHANGES;
                                            Handler.sendMessage(message);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onBad(String a) {
                                            Message message = new Message();
                                            message.what = Constant.CHANGEE;
                                            Handler.sendMessage(message);
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                break;

            case R.id.logout:
                mqttService.actionLogout(getApplicationContext());
                break;
            default:
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        Log.i("register SUBSCRIBLE ", "registering ");
        IntentFilter filter = new IntentFilter();
        filter.addAction(MqttConstant.ACTIONOFSUBBROADCAST);
        registerReceiver(broadcastReceiver, filter);
        Log.e("regist", "sucess");
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public Data getData() {
        return ((Data) getApplicationContext());
    }


}
