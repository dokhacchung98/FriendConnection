package com.example.admin.friendconnection.friend;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.action.UserActivity;
import com.example.admin.friendconnection.chat.ChatActivity;
import com.example.admin.friendconnection.chat.ChatUiActivity;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.mylocation.LocationItem;
import com.example.admin.friendconnection.object.Account;
import com.example.admin.friendconnection.schedule.AddScheludeActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 4/23/2018.
 */

public class AdapterFriendRecleview extends RecyclerView.Adapter<AdapterFriendRecleview.ViewHolder> {
    private String image;
    private Intent intent;
    private String idMe;
    private Dialog dialog;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CheckBox cbOnnOff;
        private CircleImageView imgAva;
        private TextView txtName;
        private Button btnChat;
        private Button btnInfor;
        private Button btnUnfriend;
        private Button btnLocation;
        private CardView cvHide;
        private CardView cvMain;
        private boolean checkHide = true;
        private String id;

        public ViewHolder(View itemView) {
            super(itemView);
            cbOnnOff = itemView.findViewById(R.id.checkboxOnnOffFriend);
            imgAva = itemView.findViewById(R.id.imtAvaFrienda);
            txtName = itemView.findViewById(R.id.txtFriend);
            btnChat = itemView.findViewById(R.id.btnChatFriend);
            btnInfor = itemView.findViewById(R.id.btninforFriend);
            btnLocation = itemView.findViewById(R.id.btnLocationFriend);
            btnUnfriend = itemView.findViewById(R.id.btnUnfriendFriend);
            cvHide = itemView.findViewById(R.id.cvToastFriend);
            cvMain = itemView.findViewById(R.id.cvMainFreidn);
            btnUnfriend.setOnClickListener(this);
            btnLocation.setOnClickListener(this);
            btnChat.setOnClickListener(this);
            btnInfor.setOnClickListener(this);
            cvMain.setOnClickListener(this);
           // Log.e("So phan tu", po + " la so");
        }

        public void setImage(String image) {
            Picasso.get().load(image).into(imgAva);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnChatFriend: {
                    intent = new Intent(context, ChatUiActivity.class);
                    intent.putExtra(ChatUiActivity.KEY_CODE_ID, friends.get(this.getPosition()).getPerson());
                    intent.putExtra(ChatUiActivity.KEY_CODE_IMG, accounts.get(po).getLinkAvatar());
                    intent.putExtra(ChatUiActivity.KEY_CODE_NAME, accounts.get(po).getName());
                    context.startActivity(intent);
                    break;
                }
                case R.id.btnLocationFriend: {
                    final LatLng[] latLng = new LatLng[1];
                    final int[] dem = {0};
                    Log.e("TESTTESST", friends.get(this.getPosition()).getPerson());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Location").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            LocationItem locationItem = dataSnapshot.getValue(LocationItem.class);
                            if (friends.get(getPosition()).getMode().equals(FriendFragment.ALLOW) && locationItem.getId().equals(friends.get(getPosition()).getPerson())) {
                                latLng[0] = new LatLng(Double.parseDouble(locationItem.getLat()), Double.parseDouble(locationItem.getLng()));
                            }
                            if (dem[0] == 0) {
                                showDialogMap(latLng[0]);
                                dem[0]++;
                            }
                            return;
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
                    break;
                }
                case R.id.btnUnfriendFriend: {
                    showDialogUnfriend();
                    break;
                }
                case R.id.btninforFriend: {
                    intent = new Intent(context, UserActivity.class);
                    Log.e("Id send", friends.get(this.getPosition()).getPerson());
                    intent.putExtra(LoginFragment.ID, friends.get(this.getPosition()).getPerson());
                    context.startActivity(intent);
                    break;
                }
                case R.id.cvMainFreidn: {
                    checkHide = !checkHide;
                    showCardvew();
                    break;
                }
            }
        }

        private void showDialogMap(final LatLng latLng) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_choose_map);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button btnOk = dialog.findViewById(R.id.btnOkDialogMap);
            btnOk.setVisibility(View.GONE);
            Button btnCancle = dialog.findViewById(R.id.btnCancleDialogMap);
            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });

            SearchView searchView = dialog.findViewById(R.id.searchViewDialogMap);
            searchView.setVisibility(View.GONE);
            MapView mMapView = dialog.findViewById(R.id.mapView);
            MapsInitializer.initialize(context);

            mMapView = dialog.findViewById(R.id.mapView);
            mMapView.onCreate(dialog.onSaveInstanceState());
            mMapView.onResume();// needed to get the map to display immediately
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    googleMap.setMyLocationEnabled(true);
                    final MarkerOptions markerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.location_1);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    if (latLng != null) {
                        markerOptions.position(latLng);
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                }
            });
            if (!dialog.isShowing()) {
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.show();
            }
        }

        private void showDialogUnfriend() {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.setContentView(R.layout.dialog_unfriend);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            Button btnNo = dialog.findViewById(R.id.btnNoUnfriend);
            Button btnYes = dialog.findViewById(R.id.btnYesUnfriend);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriend();
                    dialog.cancel();
                }
            });
            dialog.show();
        }

        private void deleteFriend() {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment.FRIEND);
            SharedPreferences sharedPreferences = context.getSharedPreferences(LoginFragment.FILE, Context.MODE_PRIVATE);
            idMe = sharedPreferences.getString(LoginFragment.ID, "");
            String push = friends.get(this.getPosition()).getId();
            id = friends.get(this.getPosition()).getPerson();
            databaseReference.child(idMe).child(push).removeValue();
            databaseReference.child(id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    if (friend.getPerson().equals(idMe)) {
                        databaseReference.child(id).child(friend.getId()).removeValue();
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

            databaseReference.child(id).child(push).removeValue();
            databaseReference.child(idMe).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    if (friend.getPerson().equals(id)) {
                        databaseReference.child(idMe).child(friend.getId()).removeValue();
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
            Toast.makeText(context, "delete friends successfully", Toast.LENGTH_SHORT).show();
        }

        private void showCardvew() {
            if (checkHide) {
                cvHide.animate().translationY(-1 * cvHide.getHeight()).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cvHide.setVisibility(View.GONE);
                        animation.cancel();
                    }
                }).start();
            } else {
                cvHide.setVisibility(View.VISIBLE);
                cvHide.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animation.cancel();
                    }
                }).start();
            }
        }
    }

    private ArrayList<Friend> friends;
    private Context context;
    private ArrayList<FriendOnOff> onof;
    //private int positon;
    private ArrayList<Account> accounts;
    private int po = 0;

    public AdapterFriendRecleview(ArrayList<Friend> friends, Context context, ArrayList<FriendOnOff> onof, ArrayList<Account> accounts) {
        this.friends = friends;
        this.context = context;
        this.onof = onof;
        this.accounts = accounts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_listview_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(friends.get(position).getPerson())) {
                holder.txtName.setText(accounts.get(i).getName());
                po = i;
                holder.setImage(accounts.get(i).getLinkAvatar());
                // Picasso.get().load(accounts.get(i).getLinkAvatar()).placeholder(R.drawable.user).error(R.drawable.user).centerCrop().into(holder.imgAva);
            }
        }

        for (FriendOnOff friendOnOff : onof) {
            if (friendOnOff.getId().equals(friends.get(position).getPerson())) {
                if (friendOnOff.getOnof().equals(FriendFragment.ONLINE)) {
                    holder.cbOnnOff.setChecked(true);
                } else {
                    holder.cbOnnOff.setChecked(false);
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
