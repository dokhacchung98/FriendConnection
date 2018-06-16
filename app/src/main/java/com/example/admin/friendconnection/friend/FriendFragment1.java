package com.example.admin.friendconnection.friend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.getdata.GetAccount;
import com.example.admin.friendconnection.getdata.GetUserOnOf;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Admin on 4/21/2018.
 */

public class FriendFragment1 extends Fragment {
    private RecyclerView lvFriend;
    private View view;

    private ArrayList<Friend> friends;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private AdapterFriendRecleview adapterFriend;
    private String id = "";
    public static final String FRIEND = "Friend";
    public static final String ONLINE = "Online";
    private ArrayList<FriendOnOff> onOf;
    private GetUserOnOf friendOnOff;
    private GetAccount getAccount;
    private TextView txtNodata;
    private ArrayList<Account> accounts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend1, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        lvFriend = view.findViewById(R.id.rcvFriend);
        lvFriend.setHasFixedSize(true);
        txtNodata = view.findViewById(R.id.txtNoData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        lvFriend.setLayoutManager(layoutManager);
        friends = new ArrayList<>();
        onOf = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(LoginFragment.FILE, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(LoginFragment.ID, "");
        friendOnOff = new GetUserOnOf();
        onOf = friendOnOff.getFriendOnOffs();
        accounts = new ArrayList<>();
        getAccount = new GetAccount();
        accounts = getAccount.getAccount();
        adapterFriend = new AdapterFriendRecleview(friends, getActivity(), onOf, accounts);
        lvFriend.setAdapter(adapterFriend);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getData();
        checkOnOfChange();
    }

    private void vibisilityText() {
        if (friends.size() == 0) {
            txtNodata.setVisibility(View.VISIBLE);
        } else {
            txtNodata.setVisibility(View.GONE);
        }
    }

    private void checkOnOfChange() {
        onOf.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment1.ONLINE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendOnOff friendOnOff = dataSnapshot.getValue(FriendOnOff.class);
                onOf.add(friendOnOff);
                adapterFriend.notifyDataSetChanged();
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
        databaseReference.child(FRIEND).child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                boolean check = true;
                for (int i = 0; i < friends.size(); i++) {
                    if (friend.getPerson().equals(friends.get(i).getPerson())) {
                        check = false;
                    }
                }
                if (check)
                    friends.add(friend);
                vibisilityText();
                adapterFriend.notifyDataSetChanged();
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
        vibisilityText();
    }
}
