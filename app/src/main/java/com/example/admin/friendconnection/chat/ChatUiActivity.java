package com.example.admin.friendconnection.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.getdata.GetUserOnOf;
import com.example.admin.friendconnection.login.LoginFragment;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUiActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnBack;
    private Button btnSend;
    private CircleImageView imgAva;
    private TextView txtUser;
    private ListView lvChat;
    private ArrayList<ItemChat> itemChats;
    private AdapterChat adapterChat;
    private EditText edtChat;
    private TextView txtNoData;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    public static final String CHAT = "Chat";
    public static final String KEY_CODE_ID = "fgfg";
    public static final String KEY_CODE_NAME = "fddw";
    public static final String KEY_CODE_IMG = "hrff";
    private String idMe;
    private String id;
    private String link_ava;
    private String name;
    private String link1;
    private String link2;
    private GetUserOnOf getUserOnOf;
    private ArrayList<FriendOnOff> friendOnOffs;
    private CheckBox checkOnOf;
    private SpinKitView spinKitView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ui);
        btnBack = findViewById(R.id.btnBackChat);
        btnSend = findViewById(R.id.btnSend);
        imgAva = findViewById(R.id.imgAva);
        txtUser = findViewById(R.id.txtUser);
        lvChat = findViewById(R.id.lvChat);
        edtChat = findViewById(R.id.edtChat);
        checkOnOf = findViewById(R.id.onofChat);
        txtNoData = findViewById(R.id.txtNoDataChat);
        spinKitView = findViewById(R.id.spin_kit);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        itemChats = new ArrayList<>();
        adapterChat = new AdapterChat(ChatUiActivity.this, itemChats, idMe);
        lvChat.setAdapter(adapterChat);
        Intent intent = getIntent();
        id = intent.getStringExtra(KEY_CODE_ID);
        name = intent.getStringExtra(KEY_CODE_NAME);
        link_ava = intent.getStringExtra(KEY_CODE_IMG);
        databaseReference = FirebaseDatabase.getInstance().getReference(CHAT);
        link1 = id + idMe;
        link2 = idMe + id;
        txtUser.setText(name);
        Picasso.get().load(link_ava).error(R.drawable.user).placeholder(R.drawable.user).into(imgAva);
        getValueChat();

        getUserOnOf = new GetUserOnOf();
        friendOnOffs = getUserOnOf.getFriendOnOffs();
        setOnOf();
        checkFriendOnOfChange();
        nodata();
    }

    private void nodata() {
        if (itemChats.size() <= 0) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
        }
    }


    private void checkFriendOnOfChange() {
        friendOnOffs = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment.ONLINE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendOnOff friendOnOff = dataSnapshot.getValue(FriendOnOff.class);
                friendOnOffs.add(friendOnOff);
                setOnOf();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                checkFriendOnOfChange();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                checkFriendOnOfChange();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                checkFriendOnOfChange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOnOf() {
        for (FriendOnOff friendOnOff : friendOnOffs) {
            if (friendOnOff.getOnof().equals(FriendFragment.ONLINE) && friendOnOff.getId().equals(id)) {
                checkOnOf.setChecked(true);
                return;
            }
        }
        checkOnOf.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackChat: {
                finish();
                break;
            }
            case R.id.btnSend: {
                String value = edtChat.getText().toString().trim();
                if (!value.isEmpty()) {
                    sendMes(value);
                    edtChat.setText("");
                }
                break;
            }
        }
    }

    private void getValueChat() {
        itemChats.clear();
        databaseReference.child(link1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ItemChat itemChat = dataSnapshot.getValue(ItemChat.class);
                itemChats.add(itemChat);
                adapterChat.notifyDataSetChanged();
                lvChat.setSelection(itemChats.size() - 1);
                nodata();
                spinKitView.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getValueChat();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getValueChat();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getValueChat();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getValueChat();
            }
        });
    }

    private void sendMes(String value) {
        String push = databaseReference.push().getKey();
        ItemChat itemChat = new ItemChat(push, value, idMe);
        databaseReference.child(link1).child(push).setValue(itemChat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(ChatUiActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        databaseReference.child(link2).child(push).setValue(itemChat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(ChatUiActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
