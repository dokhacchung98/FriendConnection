package com.example.admin.friendconnection.getdata;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.admin.friendconnection.friend.Friend;
import com.example.admin.friendconnection.friend.FriendFragment1;
import com.example.admin.friendconnection.login.LoginFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GetFriend {
    private ArrayList<Friend> friends;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String id;

    public ArrayList<Friend> getFriends(final String id) {
        this.id = id;
        friends = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(FriendFragment1.FRIEND).child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friends.add(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getFriends(id);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getFriends(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getFriends(id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getFriends(id);
            }

        });
        return friends;
    }

    public ArrayList<Friend> getFriend2(final Context context) {
        sharedPreferences = context.getSharedPreferences(LoginFragment.FILE, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(LoginFragment.ID, "");
        friends = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(FriendFragment1.FRIEND).child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friends.add(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getFriend2(context);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getFriend2(context);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getFriend2(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getFriend2(context);
            }

        });
        return friends;
    }
}
