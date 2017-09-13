package com.example.chao.smoke;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chao.smoke.mqtt.mqttService;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chao on 2016/12/28.
 */

public class LoginActivity extends Activity {

    /*
    控件
     */

    private EditText UAccount;
    private EditText UPassword;//用户名和密码的文本框
    private String username;
    private String password;

    private Button BSign;//注册按钮
    private ProgressBar PBSign;//进度
    private Button More;//切换界面按钮

    private CheckBox Memorize;//复选框 记住密码
    private SharedPreferences prefPassword;
    /* http post*/
    //对http返回消息的具体处理
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constant.LOGINSUCESS:
                    Data.password = password;
                    Data.username = username;
                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_LONG).show();
                    //跳转到主Activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case Constant.LOGINERRORint:
                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
                    System.out.print("error");
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        控件
         */
        //数据库初始化
        LitePal.initialize(this);
        Connector.getDatabase();
        //DataSupport.deleteAll(Device.class);

        More = (Button) findViewById(R.id.more);
        More.setOnClickListener(new myOnClickListener());
        //registerForContextMenu(More);
        UAccount = (EditText) findViewById(R.id.Account);
        UPassword = (EditText) findViewById(R.id.password);
        PBSign = (ProgressBar) findViewById(R.id.signing);
        BSign = (Button) findViewById(R.id.sign);
        Memorize = (CheckBox) findViewById(R.id.Remeber_Password);

        mqttService.actionStart(LoginActivity.this);
        //mqttService.actionSub(LoginActivity.this, "warning", 0);

        //在此处跳转
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        prefPassword = getSharedPreferences("UsernameAndPasswordFileName", MODE_PRIVATE);
        boolean isRemember = prefPassword.getBoolean(Constant.IsMemorized, false);
        //设置输入框和选择框状态
        if (isRemember) {
            UAccount.setText(prefPassword.getString(Constant.UsernameKey, ""));
            UPassword.setText(prefPassword.getString(Constant.PasswordKey, ""));
            Memorize.setChecked(true);
        } else {
            //do nothing
        }

        password = UPassword.getText().toString();
        username = UAccount.getText().toString();


//        Map<String, String> map = new HashMap<String, String>();
//        map.put("job", "login");
//        map.put("username", username);
//        map.put("password", password);
//
//        Toast.makeText(LoginActivity.this, R.string.UP_correct, Toast.LENGTH_SHORT).show();
//        Http.sendHttpRequest(Constant.HTTPADDRESS, map, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                Message message = new Message();
//                message.what = Constant.LOGINSUCESS;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onBad(String a) {
//                Message message = new Message();
//                message.what = Constant.LOGINERRORint;
//                handler.sendMessage(message);
//            }
//        });


        /*
        点击事件
         */
        BSign.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
//                                         password = UPassword.getText().toString();
//                                         username = UAccount.getText().toString();
                                         if (UserNameAndPassword.Usernamecheck(username) && UserNameAndPassword.Passwordcheck(password)) {
                                             if (Memorize.isChecked()) {
                                                 SharedPreferences.Editor editor = getSharedPreferences("UsernameAndPasswordFileName", MODE_PRIVATE).edit();
                                                 editor.putString(Constant.UsernameKey, username);
                                                 editor.putString(Constant.PasswordKey, password);
                                                 editor.putBoolean(Constant.IsMemorized, true);
                                                 editor.apply();

                                             } else {
                                                 //do nothisng
                                             }
                                             Map<String, String> map = new HashMap<String, String>();
                                             map.put("job", "login");
                                             map.put("username", username);
                                             map.put("password", password);

                                             Toast.makeText(LoginActivity.this, R.string.UP_correct, Toast.LENGTH_SHORT).show();
                                             //实现Http的回调接口
                                             Http.sendHttpRequest(Constant.HTTPADDRESS, map, new HttpCallbackListener() {
                                                 @Override
                                                 public void onFinish(String response) {
                                                     Message message = new Message();
                                                     message.what = Constant.LOGINSUCESS;
                                                     handler.sendMessage(message);
                                                 }

                                                 @Override
                                                 public void onError(Exception e) {
                                                     e.printStackTrace();
                                                 }

                                                 @Override
                                                 public void onBad(String a) {
                                                     Message message = new Message();
                                                     message.what = Constant.LOGINERRORint;
                                                     handler.sendMessage(message);
                                                 }
                                             });


                                         } else {
                                             // ........ error  informatiion
                                             Toast.makeText(LoginActivity.this, R.string.username_error, Toast.LENGTH_LONG).show();
                                         }
                                     }
                                 }

        );

    }

    /*
menu
 */
    class myOnClickListener implements View.OnClickListener {

        public void onClick(View arg0) {
            // TODO Auto-generated method stub

            openOptionsMenu();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Register:
                Intent intent = new Intent(LoginActivity.this, registerActivity.class);
                startActivity(intent);
                More.setVisibility(View.VISIBLE);
                break;
            case R.id.changeUsername:
                SharedPreferences.Editor editor = getSharedPreferences("UsernameAndPasswordFileName", MODE_PRIVATE).edit();
                editor.putBoolean(Constant.IsMemorized, false);
                editor.commit();
                Intent intent1 = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            default:
        }
        return true;
    }


}


    /*
    实现记住密码的功能
    1 在OnPause 中 记录用户名和密码
    2 在OnCreate() 中奖密码和用户名写到文件框中 （如果上次登录选择了记住密码的话 ）
     */
    /*
    protected void  OnDestory()
    {
        super.onPause();

    }
    */

