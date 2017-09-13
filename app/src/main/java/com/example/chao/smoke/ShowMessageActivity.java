package com.example.chao.smoke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 显示接收到的报警信息，根据showDeviceTab中传入的topic信息在数据库中进行筛选，显示每个topic对应的信息
 */

public class ShowMessageActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);

        Intent intent = getIntent();
        String topic = intent.getStringExtra("topic");
        Log.i("ShowMessageActivity", "topic" + topic);
        //从数据库中得到消息


        List<Message> messages = DataSupport.where("topic = ?", topic).find(Message.class);
//        List<Message> messages = DataSupport.findAll(Message.class);
        for (Message messagel: messages) {
            Log.i("ShowMessageActivity", "id:" + messagel.getId());
            Log.i("ShowMessageActivity", messagel.getMessage());
            Log.i("ShowMessageActivity", messagel.getTime());
            Log.i("ShowMessageActivity", messagel.getTopic());
            data.add(messagel.getMessage());
        }
        //反转list使新接收的信息显示在上端
        Collections.reverse(messages);
        MessageAdapter adapter = new MessageAdapter(ShowMessageActivity.this, R.layout.show_message_item, messages);
        ListView listview = (ListView) findViewById(R.id.message_list_view);
        listview.setAdapter(adapter);

    }
}
