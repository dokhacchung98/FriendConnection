package com.example.admin.friendconnection.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin.friendconnection.R;

import java.util.ArrayList;

/**
 * Created by Admin on 4/24/2018.
 */

public class AdapterChat extends ArrayAdapter<ItemChat> {
    private ArrayList<ItemChat> itemChats;
    private LayoutInflater layoutInflater;
    private TextView txtValue;
    private String idMe;

    public AdapterChat(Context context, ArrayList<ItemChat> itemChats, String idMe) {
        super(context, android.R.layout.simple_list_item_1, itemChats);
        this.itemChats = itemChats;
        this.idMe = idMe;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        for (int i = 0; i < itemChats.size(); i++) {
        }
        if (idMe.equals(itemChats.get(position).getPerson())) {
            convertView = layoutInflater.inflate(R.layout.item_chat_me, parent, false);
            txtValue = convertView.findViewById(R.id.txtChatMe);
        } else {
            convertView = layoutInflater.inflate(R.layout.item_chat_frined, parent, false);
            txtValue = convertView.findViewById(R.id.txtChatFriend);
        }
        txtValue.setText(itemChats.get(position).getValue());
        return convertView;
    }

}
