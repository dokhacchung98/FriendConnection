package com.example.admin.friendconnection.getdata;

import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GetAccount {
    private ArrayList<Account> accounts;

    public ArrayList<Account> getAccount() {
        accounts = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account friendOnOff = dataSnapshot.getValue(Account.class);
                accounts.add(friendOnOff);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAccount();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getAccount();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getAccount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return accounts;
    }
}
