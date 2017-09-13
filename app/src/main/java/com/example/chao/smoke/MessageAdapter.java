package com.example.chao.smoke;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by feihu on 2017/8/4.
 * 消息list view的自定义
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private int resourcedId;

    public MessageAdapter(Context context, int textViewResourceId, List<Message> objects) {
        super(context, textViewResourceId, objects);
        resourcedId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourcedId, parent, false);
        TextView messageId = (TextView) view.findViewById(R.id.message_id);
        TextView messageContent = (TextView) view.findViewById(R.id.message_content);
        TextView messageTime = (TextView) view.findViewById(R.id.message_time);
        TextView messageTopic = (TextView) view.findViewById(R.id.message_topic);
        messageId.setText("次数：" + message.getId());
        messageContent.setText("消息：" + message.getMessage());
        messageTime.setText("时间：" + message.getTime());
        messageTopic.setText("主题：" + message.getTopic());
        return view;
    }
}
