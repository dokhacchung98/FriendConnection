package com.example.admin.friendconnection.schedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button btnBack;
    private ListView lvSchdule;
    private FloatingActionButton floatingActionButton;
    private TextView txtNodata;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String idMe;
    private ArrayList<ScheduleItem> scheduleItems;
    private ScheduleAdapter scheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schlude);
        txtNodata=findViewById(R.id.txtNoData);
        btnBack = findViewById(R.id.btnBackSchedule);
        lvSchdule = findViewById(R.id.lvSchedule);
        floatingActionButton = findViewById(R.id.flbtnAddSchedule);
        floatingActionButton.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        lvSchdule.setOnItemClickListener(this);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        scheduleItems = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(this, scheduleItems);
        lvSchdule.setAdapter(scheduleAdapter);
        getSchdule();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackSchedule: {
                this.finish();
                break;
            }
            case R.id.flbtnAddSchedule: {
                Intent intent = new Intent(ScheduleActivity.this, AddScheludeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ScheduleActivity.this, AddScheludeActivity.class);
        ScheduleItem item = scheduleItems.get(position);
        intent.putExtra(AddScheludeActivity.DATA, item);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void getSchdule() {
        scheduleItems.clear();
        databaseReference.child(AddScheludeActivity.SCHEDULE).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ScheduleItem scheduleItem = dataSnapshot.getValue(ScheduleItem.class);
                scheduleItems.add(scheduleItem);
                scheduleAdapter.notifyDataSetChanged();
                dataChange();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getSchdule();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getSchdule();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getSchdule();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getSchdule();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }

    private void dataChange() {
        if (scheduleItems.size() == 0) {
            txtNodata.setVisibility(View.VISIBLE);
        } else {
            txtNodata.setVisibility(View.GONE);
        }
    }
}
