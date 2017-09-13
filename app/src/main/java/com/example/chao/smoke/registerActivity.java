package com.example.chao.smoke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chao on 2017/4/6.
 */

public class registerActivity extends Activity {
    private EditText RAccount;
    private EditText RPassword;//用户名和密码的文本框
    private Button RRegister;
    private String Rusername;
    private String Rpassword;

    private Button BSign;//注册按钮
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.REGISTERSUCCESS:
                    Intent intent = new Intent(registerActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case Constant.REGISTERBAD:
                    Toast.makeText(registerActivity.this, "username has registered ", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        RAccount = (EditText) findViewById(R.id.registerAccount);
        RPassword = (EditText) findViewById(R.id.registerpassword);
        RRegister = (Button) findViewById(R.id.register);
        RRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rpassword = RPassword.getText().toString();
                Rusername = RAccount.getText().toString();
                if (UserNameAndPassword.Usernamecheck(Rusername) && UserNameAndPassword.Passwordcheck(Rpassword)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("job", "register");
                    map.put("username", Rusername);
                    map.put("password", Rpassword);

                    Toast.makeText(registerActivity.this, "you are trying register", Toast.LENGTH_SHORT).show();
                    Http.sendHttpRequest(Constant.HTTPADDRESS, map, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            Message message = new Message();
                            message.what = Constant.REGISTERSUCCESS;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onBad(String a) {
                            Message message = new Message();
                            message.what = Constant.REGISTERBAD;
                            handler.sendMessage(message);
                        }
                    });
                } else {
                    Toast.makeText(registerActivity.this, R.string.username_error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
