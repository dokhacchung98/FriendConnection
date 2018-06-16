package com.example.admin.friendconnection.getdata;

import android.app.Activity;

import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Admin on 4/23/2018.
 */

public class GetUserOnOf {
    private ArrayList<FriendOnOff> friendOnOffs;

    public ArrayList<FriendOnOff> getFriendOnOffs() {
        friendOnOffs = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment.ONLINE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendOnOff friendOnOff = dataSnapshot.getValue(FriendOnOff.class);
                friendOnOffs.add(friendOnOff);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getFriendOnOffs();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getFriendOnOffs();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getFriendOnOffs();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return friendOnOffs;
    }
}
