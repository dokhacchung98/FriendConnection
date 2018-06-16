package com.example.admin.friendconnection.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.friend.Friend;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatHome extends ArrayAdapter<Friend> {
    private ArrayList<Friend> friends;
    private DatabaseReference databaseReference;
    private CircleImageView imgAva;
    private TextView txtName;
    private CheckBox cbOnOf;
    private LayoutInflater layoutInflater;
    private ArrayList<FriendOnOff> onof;
    private ArrayList<Account> accounts;

    public AdapterChatHome(@NonNull Context context, ArrayList<Friend> friends, DatabaseReference databaseReference, ArrayList<FriendOnOff> onof, ArrayList<Account> accounts) {
        super(context, android.R.layout.simple_list_item_1, friends);
        this.friends = friends;
        this.databaseReference = databaseReference;
        this.onof = onof;
        this.accounts = accounts;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_chat_home_listview, parent, false);
        imgAva = convertView.findViewById(R.id.imtAvaFriend);
        txtName = convertView.findViewById(R.id.txtFriend);
        cbOnOf = convertView.findViewById(R.id.checkboxOnnOffChatHome);

        for (Account account : accounts) {
            if (account.getId().equals(friends.get(position).getPerson())) {
                Picasso.get().load(account.getLinkAvatar()).placeholder(R.drawable.user).error(R.drawable.user).fit().centerInside().into(imgAva);
                txtName.setText(account.getName());
            }
        }

        for (FriendOnOff friendOnOff : onof) {
            if (friendOnOff.getId().equals(friends.get(position).getPerson())) {
                Log.e("Id", friendOnOff.getOnof());
                if (friendOnOff.getOnof().equals(FriendFragment.ONLINE)) {
                    cbOnOf.setChecked(true);
                } else if (friendOnOff.getOnof().equals(FriendFragment.OFFLINE)) {
                    cbOnOf.setChecked(false);
                }
                break;
            }
        }
        return convertView;
    }
}