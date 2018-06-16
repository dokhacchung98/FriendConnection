package com.example.admin.friendconnection.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAddFriend extends ArrayAdapter<String> {
    private ArrayList<String> addFriends;
    private LayoutInflater layoutInflater;
    private CircleImageView imgAva;
    private TextView txtName;
    private Button btnAllow;
    private Button btnUnallow;
    private DatabaseReference databaseReference;
    private String idMe;

    public AdapterAddFriend(Context context, ArrayList<String> resource, DatabaseReference databaseReference, String idMe) {
        super(context, android.R.layout.simple_list_item_1, resource);
        this.addFriends = resource;
        this.databaseReference = databaseReference;
        this.idMe = idMe;
        layoutInflater = LayoutInflater.from(context);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_add_friend_listview, parent, false);
        imgAva = convertView.findViewById(R.id.imtAvaFriendAdd);
        txtName = convertView.findViewById(R.id.txtFriendAdd);
        btnAllow = convertView.findViewById(R.id.btnAllowFriend);
        btnUnallow = convertView.findViewById(R.id.btnUnAllowFriend);
        databaseReference.child("Account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account temp = dataSnapshot.getValue(Account.class);
                if(temp.getId().equals(addFriends.get(position))){
                    txtName.setText(temp.getName());
                    Picasso.get().load(temp.getLinkAvatar()).placeholder(R.drawable.user).error(R.drawable.user).into(imgAva);
                }
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
        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowFriend(addFriends.get(position));
            }
        });
        btnUnallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unallow(addFriends.get(position));
            }
        });
        return convertView;
    }

    private void unallow(String key) {
        this.clear();
        databaseReference.child(AddFriendFragment.ADDFRIEND).child(idMe).child(key).removeValue();
    }

    private void allowFriend(String key) {
        unallow(key);
        String push = databaseReference.push().getKey();
        Friend friend = new Friend(push, key, FriendFragment.ALLOW);
        databaseReference.child(FriendFragment.FRIEND).child(idMe).child(push).setValue(friend, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Friend friend1 = new Friend(push, idMe, FriendFragment.ALLOW);
        databaseReference.child(FriendFragment.FRIEND).child(key).child(push).setValue(friend1, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
