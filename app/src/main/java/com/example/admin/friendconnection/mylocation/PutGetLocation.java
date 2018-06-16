package com.example.admin.friendconnection.mylocation;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PutGetLocation {
    private ArrayList<LocationItem> locationItems;
    private DatabaseReference databaseReference;
    public static final String LOCATION = "Location";
    public static final String KEY = "key";
    public static final String ID = "id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    public PutGetLocation() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        locationItems = new ArrayList<>();
    }

    public ArrayList<LocationItem> getLocationItems() {
        databaseReference.child(LOCATION).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LocationItem locationItem = dataSnapshot.getValue(LocationItem.class);
                locationItems.add(locationItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return locationItems;
    }
}
