package com.example.admin.friendconnection.friend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 4/22/2018.
 */

public class AdapterFriend extends ArrayAdapter<Friend> implements View.OnClickListener {
    private LayoutInflater layoutInflater;
    private boolean cvHide = true;
    private ViewHolder viewHolder;
    private ArrayList<Friend> friends;
    private ArrayList<FriendOnOff> onof;

    public AdapterFriend(@NonNull Context context, ArrayList<Friend> objects, ArrayList<FriendOnOff> onof) {
        super(context, android.R.layout.simple_list_item_1, objects);
        layoutInflater = LayoutInflater.from(getContext());
        friends = objects;
        this.onof = onof;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_listview_friend, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.cbOnnOff = convertView.findViewById(R.id.checkboxOnnOffFriend);
        viewHolder.imgAva = convertView.findViewById(R.id.imtAvaFriend);
        viewHolder.txtName = convertView.findViewById(R.id.txtFriend);
        viewHolder.btnChat = convertView.findViewById(R.id.btnChatFriend);
        viewHolder.btnInfor = convertView.findViewById(R.id.btninforFriend);
        viewHolder.btnLocation = convertView.findViewById(R.id.btnLocationFriend);
        viewHolder.btnUnfriend = convertView.findViewById(R.id.btnUnfriendFriend);
        viewHolder.cvHide = convertView.findViewById(R.id.cvToastFriend);
        viewHolder.cvMain = convertView.findViewById(R.id.cvMainFreidn);
        viewHolder.btnUnfriend.setOnClickListener(this);
        viewHolder.btnLocation.setOnClickListener(this);
        viewHolder.btnChat.setOnClickListener(this);
        viewHolder.btnInfor.setOnClickListener(this);
        viewHolder.cvMain.setOnClickListener(this);
        /**Xử lý kiểm tra online hay offline*/
        for (FriendOnOff friendOnOff : onof) {
            if (friendOnOff.getId().equals(friends.get(position).getPerson())) {
                Log.e("Id", friendOnOff.getOnof());
                if (friendOnOff.getOnof().equals(FriendFragment.ONLINE)) {
                    viewHolder.cbOnnOff.setChecked(true);
                } else {
                    viewHolder.cbOnnOff.setChecked(false);
                }
                break;
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChatFriend: {
                break;
            }
            case R.id.btnLocationFriend: {
                break;
            }
            case R.id.btnUnfriendFriend: {
                break;
            }
            case R.id.btninforFriend: {
                break;
            }
            case R.id.cvMainFreidn: {
                cvHide = !cvHide;
                showCardvew();
                break;
            }
        }
    }

    private void showCardvew() {
        if (cvHide) {
            viewHolder.cvHide.animate().translationY(-1 * viewHolder.cvHide.getHeight()).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    viewHolder.cvHide.setVisibility(View.GONE);
                    animation.cancel();
                }
            }).start();
        } else {
            viewHolder.cvHide.setVisibility(View.VISIBLE);
            viewHolder.cvHide.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animation.cancel();
                }
            }).start();
        }
    }

    public class ViewHolder {
        private CheckBox cbOnnOff;
        private CircleImageView imgAva;
        private TextView txtName;
        private Button btnChat;
        private Button btnInfor;
        private Button btnUnfriend;
        private Button btnLocation;
        private CardView cvHide;
        private CardView cvMain;
    }
}
