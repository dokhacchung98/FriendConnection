package com.example.admin.friendconnection.home;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.MyService;
import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.action.UserActivity;
import com.example.admin.friendconnection.chat.ChatActivity;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.login.LoginActivity;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.friend.FriendActivity;
import com.example.admin.friendconnection.mylocation.MapsActivity;
import com.example.admin.friendconnection.object.Account;
import com.example.admin.friendconnection.schedule.NotiScheduleActivity;
import com.example.admin.friendconnection.schedule.ScheduleActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener {

    private long timePressBack;
    private Button btnPosition;
    private Button btnFriend;
    private Button btnUser;
    private Button btnMes;
    private Button btnLogout;
    private Button btnSchedule;
    private CircleImageView imgAva;
    private TextView txtName;
    private SharedPreferences sharedPreferences;
    private String linkAva;
    private String name;
    private String id;
    private Intent intent;
    private DatabaseReference databaseReference;
    public static final String ACCOUNT = "Account";
    public static final String USER = "userName";
    public static final String PASS = "passWord";
    public static final String ID = "id";
    public static final String AVATAR = "linkAvatar";
    public static final String MAIL = "mail";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String SEX = "sex";
    private Account account;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_again);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        linkAva = sharedPreferences.getString(LoginFragment.LINK, "");
        name = sharedPreferences.getString(LoginFragment.NAME, "");
        id = sharedPreferences.getString(LoginFragment.ID, "");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        changeOnline();

        btnPosition = findViewById(R.id.btnPosition);
        btnFriend = findViewById(R.id.btnFriend);
        btnUser = findViewById(R.id.btnUser);
        btnMes = findViewById(R.id.btnMes);
        btnLogout = findViewById(R.id.btnLogout);
        btnSchedule = findViewById(R.id.btnSchedule);
        imgAva = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);

        txtName.setText(name);

        Intent i = new Intent(this, MyService.class);
        if (!isServiceRunning()) {startService(i);
        }
        Picasso.get().load(linkAva).placeholder(R.drawable.user).into(imgAva);
        btnPosition.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnMes.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnSchedule.setOnClickListener(this);

        update();
    }

    private void changeOnline() {
        FriendOnOff friendOnOff = new FriendOnOff(id, FriendFragment.ONLINE);
        databaseReference.child(FriendFragment.ONLINE).child(id).setValue(friendOnOff);
    }

    @Override
    public void onBackPressed() {
        if (timePressBack + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        timePressBack = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPosition: {
                intent = new Intent(ActivityHome.this, MapsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnFriend: {
                intent = new Intent(ActivityHome.this, FriendActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnUser: {
                intent = new Intent(ActivityHome.this, UserActivity.class);
                intent.putExtra(LoginFragment.ID, id);
                startActivity(intent);
                break;
            }
            case R.id.btnMes: {
                intent = new Intent(ActivityHome.this, ChatActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnLogout: {
                showDialogLogout();
                break;
            }
            case R.id.btnSchedule: {
                intent = new Intent(ActivityHome.this, ScheduleActivity.class);
                startActivity(intent);
                break;
            }
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void showDialogLogout() {
        final Dialog dialog = new Dialog(ActivityHome.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        Button btnCancle = dialog.findViewById(R.id.btnCancleLogout);
        Button btnYes = dialog.findViewById(R.id.btnYesLogout);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlingLogout();
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void handlingLogout() {
        changeOffline();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginFragment.ID, "");
        editor.putString(LoginFragment.BIRTH, "");
        editor.putString(LoginFragment.LINK, "");
        editor.putString(LoginFragment.MAIL, "");
        editor.putString(LoginFragment.NAME, "");
        editor.putString(LoginFragment.PASS, "");
        editor.putString(LoginFragment.PHONE, "");
        editor.putString(LoginFragment.SEX, "");
        editor.putString(LoginFragment.USER, "");
        if (isServiceRunning()) {
            Intent myService = new Intent(ActivityHome.this, MyService.class);
            stopService(myService);
        }
        Intent intent = new Intent(ActivityHome.this, LoginActivity.class);
        startActivity(intent);
        ActivityHome.this.finish();
    }

    private void changeOffline() {
        FriendOnOff friendOnOff = new FriendOnOff(id, FriendFragment.OFFLINE);
        databaseReference.child(FriendFragment.ONLINE).child(id).setValue(friendOnOff);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        changeOffline();
    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.admin.friendconnection.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void update() {
        databaseReference.child("Account").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account = dataSnapshot.getValue(Account.class);
                Picasso.get().load(account.getLinkAvatar()).error(R.drawable.user).placeholder(R.drawable.user).into(imgAva);
                txtName.setText(account.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                update();
            }
        });
    }
}
