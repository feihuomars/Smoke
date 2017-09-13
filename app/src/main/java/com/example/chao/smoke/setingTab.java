package com.example.chao.smoke;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.MqttConstant;
import com.example.chao.smoke.mqtt.mqttService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chao on 2017/5/1.
 * 设置界面
 */

public class setingTab extends Fragment {
    @Nullable
    private android.os.Handler Handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case MqttConstant.LOGOUTSUCESS:
                    getData().topicList.clear();
                    Toast.makeText(getActivity(),MqttConstant.LOGOUTSUCESSINFO,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case MqttConstant.LOGOUTERROR:
                    Toast.makeText(getActivity(),"注销失败",Toast.LENGTH_LONG).show();
                case Constant.CHANGES:
                    Toast.makeText(getActivity(),"修改密码成功",Toast.LENGTH_LONG).show();
                    getData().topicList.clear();
                    mqttService.actionLogout(getActivity().getApplicationContext());
                    Intent intent1 = new Intent(getActivity(),LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    break;
                case Constant.CHANGEE:
                    Toast.makeText(getActivity(),"修改密码失败",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver setbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(MqttConstant.SubState,MqttConstant.SUBSCRIBING);
            Message  message= new Message();
            message.what=state;
            Handler.sendMessage(message);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settinglayout,container,false);
        RelativeLayout change = (RelativeLayout)view.findViewById(R.id.change);
        RelativeLayout logout = (RelativeLayout)view.findViewById(R.id.tuichu);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View alter = li.inflate(R.layout.alter,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(alter);
                final EditText userInput = (EditText)alter.findViewById(R.id.editTextDialogUserInput);
                final EditText userSure = (EditText)alter.findViewById(R.id.MakeSureDialogUserInput);
                builder.setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = userInput.getText().toString();
                                String rpassword = userSure.getText().toString();
                                if(password!=null && password.equals(rpassword))
                                {
                                    Map<String ,String> map = new HashMap<String, String>();
                                    map.put("job","change");
                                    map.put("username",Data.username);
                                    map.put("password",Data.password);
                                    map.put("newpassword",password);
                                    Http.sendHttpRequest(Constant.HTTPADDRESS, map, new HttpCallbackListener() {
                                        @Override
                                        public void onFinish(String response) {
                                            Message message  = new Message();
                                            message.what=Constant.CHANGES;
                                            Handler.sendMessage(message);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onBad(String a) {
                                            Message message  = new Message();
                                            message.what=Constant.CHANGEE;
                                            Handler.sendMessage(message);
                                        }
                                    });
                                }
                                else
                                {
                                    Message message  = new Message();
                                    message.what=Constant.ALTERERROR;
                                    Handler.sendMessage(message);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttService.actionLogout(getActivity().getApplicationContext());
            }
        });
        return view;
    }
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction((MqttConstant.ACTIONOFSUBBROADCAST));
        getActivity().registerReceiver(setbroadcastReceiver,filter);
    }
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(setbroadcastReceiver);
    }
    public Data getData()
    {
        return ((Data)getActivity().getApplicationContext());
    }
}
