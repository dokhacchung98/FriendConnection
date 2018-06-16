package com.example.admin.friendconnection.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.friend.Friend;
import com.example.admin.friendconnection.friend.FriendFragment1;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.getdata.GetAccount;
import com.example.admin.friendconnection.getdata.GetUserOnOf;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lvChatHome;
    private ArrayList<Friend> friends;
    private DatabaseReference databaseReference;
    private ArrayList<FriendOnOff> onof;
    private AdapterChatHome adapterChatHome;
    private String idMe;
    private SharedPreferences sharedPreferences;
    private GetUserOnOf getUserOnOf;
    private ArrayList<Account> accounts;
    private TextView txtNo;
    private Button btnBack;
    private GetAccount getAccount;
    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        txtNo = findViewById(R.id.txtNoData);
        lvChatHome = findViewById(R.id.lvchatH);
        lvChatHome.setItemsCanFocus(false);
        friends = new ArrayList<>();
        onof = new ArrayList<>();
        accounts = new ArrayList<>();
        btnBack = findViewById(R.id.btnBackChat);
        spinKitView = findViewById(R.id.spin_kit);
        btnBack.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        getUserOnOf = new GetUserOnOf();
        onof = getUserOnOf.getFriendOnOffs();
        getAccount = new GetAccount();
        accounts = getAccount.getAccount();
        adapterChatHome = new AdapterChatHome(ChatActivity.this, friends, databaseReference, onof, accounts);
        lvChatHome.setAdapter(adapterChatHome);
        lvChatHome.setOnItemClickListener(this);

        getData();
        checkOnOfChange();
        //getAccoutInFirebase();
    }

    private void checkOnOfChange() {
        onof.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment1.ONLINE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendOnOff friendOnOff = dataSnapshot.getValue(FriendOnOff.class);
                onof.add(friendOnOff);
                adapterChatHome.notifyDataSetChanged();
                spinKitView.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                checkOnOfChange();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                checkOnOfChange();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                checkOnOfChange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getData() {
        friends.clear();
        databaseReference.child(FriendFragment1.FRIEND).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friends.add(friend);
                adapterChatHome.notifyDataSetChanged();
                vibisilitytext();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getData();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getData();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getData();
            }
        });
    }

    private void getAccoutInFirebase() {
        accounts.clear();
        databaseReference.child("Account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account account = dataSnapshot.getValue(Account.class);
                accounts.add(account);
                adapterChatHome.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAccoutInFirebase();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getAccoutInFirebase();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getAccoutInFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getAccoutInFirebase();
            }
        });
    }

    private void vibisilitytext() {
        if (friends.size() == 0) {
            txtNo.setVisibility(View.VISIBLE);
        } else {
            txtNo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBackChat) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Account acc = new Account();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(friends.get(position).getPerson())) {
                acc = accounts.get(i);
            }
        }
        Intent intent = new Intent(ChatActivity.this, ChatUiActivity.class);
        intent.putExtra(ChatUiActivity.KEY_CODE_ID, acc.getId());
        intent.putExtra(ChatUiActivity.KEY_CODE_IMG, acc.getLinkAvatar());
        intent.putExtra(ChatUiActivity.KEY_CODE_NAME, acc.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}