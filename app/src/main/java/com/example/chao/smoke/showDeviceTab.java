package com.example.chao.smoke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by chao on 2017/5/1.
 * 显示设备列表
 */

public class showDeviceTab extends ListFragment {
    View view;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.listview, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);


        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        if (getData().topicList != null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData().topicList);
            if (listView != null) {
                listView.setAdapter(arrayAdapter);
            }
        } else {
            Log.i("hello", "ajsd");
        }
        //每个item子项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String topic = getData().topicList.get(i);
                //启动showMessageActivity，传入topic参数
                Intent intent = new Intent(getActivity(), ShowMessageActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);

                Toast.makeText(getActivity(), topic, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Data getData() {

        return ((Data) getActivity().getApplicationContext());
    }
}
