package com.example.admin.friendconnection.mylocation;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.chat.ChatUiActivity;
import com.example.admin.friendconnection.friend.Friend;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendFragment1;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.getdata.GetAccount;
import com.example.admin.friendconnection.getdata.GetUserOnOf;
import com.example.admin.friendconnection.object.Account;
import com.example.admin.friendconnection.schedule.AddScheludeActivity;
import com.example.admin.friendconnection.schedule.ScheduleItem;
import com.example.admin.friendconnection.schedule.TempSchedule;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {
    private View view;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    public static final int REQUES_INT = 123;
    private float ZOOM_LEVEL = 10;
    private LocationManager locationManager;
    private ArrayList<Friend> friends;
    private ArrayList<ScheduleItem> scheduleItems;
    private String idMe;
    private ArrayList<LocationItem> locationItems;
    private DatabaseReference databaseReference;
    private Location myLocation;
    private ArrayList<Account> accounts;
    private GetAccount getAccount;
    private GetUserOnOf getUserOnOf;
    private ArrayList<FriendOnOff> friendOnOffs;
    private boolean changeMap = true;
    private boolean moveMap = true;
    private CardView cvInfo;
    private TextView txtTime;
    private TextView txtDistance;
    private int mapType;
    private int tempJ;
    private MarkerOptions markerOptions;
    private MarkerOptions markerOptionsSchedule;
    private Bitmap smallMarkerSchedule;
    private Bitmap smallMarkerLocation;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LatLng tempLatlng = null;
    private TempSchedule tempSchedule = new TempSchedule();


    @SuppressLint("ValidFragment")
    public MapFragment(String idMe) {
        this.idMe = idMe;
    }

    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        cvInfo = view.findViewById(R.id.cvInfor);
        txtTime = view.findViewById(R.id.txtTime);
        txtDistance = view.findViewById(R.id.txtDistance);

        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, supportMapFragment).commit();
        }

        supportMapFragment.getMapAsync(this);
        locationItems = new ArrayList<>();
        accounts = new ArrayList<>();
        getAccount = new GetAccount();
        accounts = getAccount.getAccount();
        getUserOnOf = new GetUserOnOf();
        friendOnOffs = new ArrayList<>();
        friendOnOffs = getUserOnOf.getFriendOnOffs();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        changeMapStyle(mapType);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_2);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarkerSchedule = Bitmap.createScaledBitmap(b, 150, 150, false);

        BitmapDrawable bitmapdrawa = (BitmapDrawable) getResources().getDrawable(R.drawable.location_1);
        Bitmap ba = bitmapdrawa.getBitmap();
        smallMarkerLocation = Bitmap.createScaledBitmap(ba, 150, 150, false);

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUES_INT);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                cvInfo.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cvInfo.setVisibility(View.GONE);
                        animation.cancel();
                    }
                }).start();
            }
        });
        getFriend();
        //onOfChange();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
//        Location location = locationManager.getLastKnownLocation(provider);
//        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //  Log.e("MAPMAP", location.getLatitude() + " - " + location.getLongitude());
                if (moveMap) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    moveMap = false;
                }
                myLocation = location;
            }
        });
        if (myLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL), 2000, null);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
        }
        showMakerFriend();
        onOfChange();
        mMap.setOnMarkerClickListener(this);
    }

    public void changeMapStyle(int k) {
        if (mMap != null) {
            switch (k) {
                case 1: {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                }
                case 2: {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                }
                case 3: {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                }
            }
        } else {
            switch (k) {
                case 1: {
                    mapType = 1;
                    break;
                }
                case 2: {
                    mapType = 2;
                    break;
                }
                case 3: {
                    mapType = 3;
                    break;
                }
            }
        }
    }

    private void getFriend() {
        getFriendFirebase();
//        getFriend = new GetFriend();
//        friends = getFriend.getFriend2(getActivity());

    }

    private void getLocationFriend() {
        locationItems.clear();
        databaseReference.child(PutGetLocation.LOCATION).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LocationItem locationItem = dataSnapshot.getValue(LocationItem.class);
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).getPerson().equals(locationItem.getId()) && friends.get(i).getMode().equals(FriendFragment.ALLOW)) {
                        locationItems.add(locationItem);
                        break;
                    }
                }
                /**update lại khi có sự thay đổi*/
                if (changeMap) {
                    showMakerFriend();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getLocationFriend();
                changeMap = false;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getLocationFriend();
                changeMap = false;

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getLocationFriend();
                changeMap = false;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getLocationFriend();
                changeMap = false;
            }
        });
    }

    String name = "";
    String linkava = "";
    boolean onof = false;
    MyInfoWindowAdapter myInfoWindowAdapter;
    MyInfoWindowAdapterSchedule myInfoWindowAdapterSchedule;

    public void showMarkerSchedule() {
        for (int i = 0; i < scheduleItems.size(); i++) {
            if (markerOptionsSchedule == null)
                markerOptionsSchedule = new MarkerOptions();

//            myInfoWindowAdapterSchedule = new MyInfoWindowAdapterSchedule(
//                    scheduleItems.get(i).getTitle(), scheduleItems.get(i).getTime(), scheduleItems.get(i).getCalendar(), scheduleItems.get(i).getValue()
//            );
            markerOptionsSchedule.icon(BitmapDescriptorFactory.fromBitmap(smallMarkerSchedule));
            markerOptionsSchedule.position(new LatLng(Double.parseDouble(scheduleItems.get(i).getLat()), Double.parseDouble(scheduleItems.get(i).getLng())));
            Marker marker1 = mMap.addMarker(markerOptionsSchedule);
            Log.e("TAGTAG", scheduleItems.get(i).getId());
            marker1.setTag(scheduleItems.get(i));
            // markerOptionsScheduless.add(i,)

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                }
            });
            //     mMap.setInfoWindowAdapter(myInfoWindowAdapterSchedule);
//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    Toast.makeText(getActivity(), marker.getPosition().latitude + " - " + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
//                    if (myLocation != null) {
//                        showMakerFriend();
//                        markerOptionsSchedule.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                        markerOptionsSchedule.position(new LatLng(Double.parseDouble(scheduleItems.get(tempI).getLat()), Double.parseDouble(scheduleItems.get(tempI).getLng())));
//                        marker = mMap.addMarker(markerOptionsSchedule);
//                        marker.showInfoWindow();
//                        sendRequest(marker.getPosition().latitude + "", marker.getPosition().longitude + "", myLocation.getLatitude() + "", myLocation.getLongitude() + "");
//                        cvInfo.setVisibility(View.VISIBLE);
//                        cvInfo.animate().translationY((float) (-0.5 * cvInfo.getHeight())).setDuration(100).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animation.cancel();
//                            }
//                        }).start();
//                    }
//                    return false;
//                }
//            });
        }
    }

    public void showMakerFriend() {
        mMap.clear();
        showMarkerSchedule();

        if (tempLatlng != null) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_3);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
            MarkerOptions options=new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            options.position(tempLatlng);
            Marker marker = mMap.addMarker(options);
            marker.setTag(tempSchedule);
            InfoAddSchedule infoAddSchedule = new InfoAddSchedule();
            mMap.setInfoWindowAdapter(infoAddSchedule);
        }

        for (int i = 0; i < locationItems.size(); i++) {
            for (int j = 0; j < accounts.size(); j++) {
                tempJ = j;
                if (locationItems.get(i).getId().equals(accounts.get(j).getId())) {
                    name = accounts.get(j).getName();
                    linkava = accounts.get(j).getLinkAvatar();
                    tempJ = j;
                    break;
                }
            }

            markerOptions = new MarkerOptions();

            markerOptions.position(new LatLng(Double.parseDouble(locationItems.get(i).getLat()), Double.parseDouble(locationItems.get(i).getLng())));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarkerLocation));
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(locationItems.get(i));

//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    if (myLocation != null) {
//                        showMakerFriend();
//                        marker = mMap.addMarker(markerOptions);
//                        marker.showInfoWindow();
//                        sendRequest(marker.getPosition().latitude + "", marker.getPosition().longitude + "", myLocation.getLatitude() + "", myLocation.getLongitude() + "");
//                        cvInfo.setVisibility(View.VISIBLE);
//                        cvInfo.animate().translationY((float) (-0.5 * cvInfo.getHeight())).setDuration(100).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animation.cancel();
//                            }
//                        }).start();
//                    }
//                    return false;
//                }
//            });
//            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//
//                }
//            });

//            if (!moveMap)
//                markers.showInfoWindow();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        cvInfo.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvInfo.setVisibility(View.GONE);
                animation.cancel();
            }
        }).start();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        cvInfo.setVisibility(View.VISIBLE);
        cvInfo.animate().translationY((float) (-0.5 * cvInfo.getHeight())).setDuration(100).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.cancel();
            }
        }).start();
        if (myLocation != null) {
            sendRequest(marker.getPosition().latitude + "", marker.getPosition().longitude + "", myLocation.getLatitude() + "", myLocation.getLongitude() + "");
        }
        if (marker.getTag().getClass().toString().equals(ScheduleItem.class.toString())) {
            //  Toast.makeText(getActivity(), "trùng schedule", Toast.LENGTH_SHORT).show();
            checkSchedule((ScheduleItem) marker.getTag());
        } else if (marker.getTag().getClass().toString().equals(LocationItem.class.toString())) {
            checkLocation((LocationItem) marker.getTag());
        } else if (marker.getTag().getClass().toString().equals(TempSchedule.class.toString())) {
            checkAddNewSchedule(marker.getPosition());
        } else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkAddNewSchedule(final LatLng position) {
        if (position != null) {
            showMakerFriend();
            InfoAddSchedule infoAddSchedule = new InfoAddSchedule();
            mMap.setInfoWindowAdapter(infoAddSchedule);
            final MarkerOptions markerOptions = new MarkerOptions();
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_3);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            markerOptions.position(position);
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
            final String name = getNameAddress(position);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getActivity(), AddScheludeActivity.class);
                    intent.putExtra(AddScheludeActivity.LNGG, position.longitude + "");
                    intent.putExtra(AddScheludeActivity.LATTT, position.longitude + "");
                    intent.putExtra(AddScheludeActivity.LOCA, name);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }
    }

    private void checkLocation(LocationItem tag) {
        if (tag != null) {
            showMakerFriend();

            for (int j = 0; j < accounts.size(); j++) {
                tempJ = j;
                if (tag.getId().equals(accounts.get(j).getId())) {
                    name = accounts.get(j).getName();
                    linkava = accounts.get(j).getLinkAvatar();
                    tempJ = j;
                    break;
                }
            }
            markerOptions = new MarkerOptions();

            myInfoWindowAdapter = new MyInfoWindowAdapter(linkava, name, onof, tag.getId());

            markerOptions.position(new LatLng(Double.parseDouble(tag.getLat()), Double.parseDouble(tag.getLng())));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarkerLocation));
            myInfoWindowAdapter = new MyInfoWindowAdapter(linkava, name, onof, tag.getId());
            mMap.setInfoWindowAdapter(myInfoWindowAdapter);
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getContext(), ChatUiActivity.class);
                    intent.putExtra(ChatUiActivity.KEY_CODE_ID, accounts.get(tempJ).getId());
                    intent.putExtra(ChatUiActivity.KEY_CODE_IMG, accounts.get(tempJ).getLinkAvatar());
                    intent.putExtra(ChatUiActivity.KEY_CODE_NAME, accounts.get(tempJ).getName());
                    getContext().startActivity(intent);
                }
            });
        } else {
            Log.e("SSSSS", "Thôi xong ><!");
        }
    }

    private void checkSchedule(final ScheduleItem tag) {
        showMakerFriend();
        if (tag != null) {
            myInfoWindowAdapterSchedule = new MyInfoWindowAdapterSchedule(
                    tag.getTitle(), tag.getTime(), tag.getCalendar(), tag.getValue()
            );
            mMap.setInfoWindowAdapter(myInfoWindowAdapterSchedule);
            markerOptionsSchedule = new MarkerOptions();
            markerOptionsSchedule.icon(BitmapDescriptorFactory.fromBitmap(smallMarkerSchedule));
            markerOptionsSchedule.position(new LatLng(Double.parseDouble(tag.getLat()), Double.parseDouble(tag.getLng())));
            Marker marker = mMap.addMarker(markerOptionsSchedule);
            marker.showInfoWindow();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getContext(), AddScheludeActivity.class);
                    ScheduleItem item = tag;
                    intent.putExtra(AddScheludeActivity.DATA, item);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("SSSSS", "nul cmnr");
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final MarkerOptions markerOptions = new MarkerOptions();
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_3);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        markerOptions.position(latLng);
        Marker marker = mMap.addMarker(markerOptions);
        tempLatlng = latLng;
        showMakerFriend();
        marker.setTag(tempSchedule);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    private String getNameAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    class MyInfoWindowAdapterSchedule implements GoogleMap.InfoWindowAdapter {
        private View myView;
        private String title;
        private String time;
        private String calendar;
        private String v;

        public MyInfoWindowAdapterSchedule(String title, String time, String calendar, String value) {
            myView = getLayoutInflater().inflate(R.layout.window_info_schedule, null);
            this.title = title;
            this.time = time;
            this.calendar = calendar;
            this.v = value;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView txtTitle = myView.findViewById(R.id.txtTitle);
            TextView txtTime = myView.findViewById(R.id.txtTime);
            TextView txtCalendar = myView.findViewById(R.id.txtCalendar);
            TextView txtValue = myView.findViewById(R.id.txtvalue);
            txtTitle.setText(title);
            txtTime.setText(time);
            txtCalendar.setText(calendar);
            txtValue.setText(v);
            return myView;
        }
    }

    class InfoAddSchedule implements GoogleMap.InfoWindowAdapter {
        private View myContentsView;

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        public InfoAddSchedule() {
            myContentsView = getLayoutInflater().inflate(R.layout.window_info_add, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return myContentsView;
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View myContentsView;
        private String linkAva;
        private String name;
        private boolean onof;
        private String id;
        private GetUserOnOf getUserOnOf;
        private ArrayList<FriendOnOff> friendOnOffs;

        @SuppressLint("InflateParams")
        MyInfoWindowAdapter(String linkAva, String name, boolean onof, String id) {
            myContentsView = getLayoutInflater().inflate(R.layout.window_info, null);
            this.linkAva = linkAva;
            this.name = name;
            this.onof = onof;
            this.id = id;
            getUserOnOf = new GetUserOnOf();
            friendOnOffs = new ArrayList<>();
            friendOnOffs = getUserOnOf.getFriendOnOffs();
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getInfoContents(Marker marker) {

            CircleImageView imgAva = myContentsView.findViewById(R.id.imgAvaMap);
            TextView txtName = myContentsView.findViewById(R.id.txtNameMap);
            CheckBox cbOnOf = myContentsView.findViewById(R.id.cbOnOfMap);
            TextView txtLat = myContentsView.findViewById(R.id.txtLat);
            TextView txtLng = myContentsView.findViewById(R.id.txtLng);

            Picasso.get().load(linkAva).placeholder(R.drawable.user).into(imgAva);
            txtName.setText(name);

            onof = false;
            for (FriendOnOff friendOnOff : friendOnOffs) {
                if (friendOnOff.getId().equals(id)) {
                    if (friendOnOff.getOnof().equals(FriendFragment.ONLINE)) {
                        onof = true;
                        break;
                    }
                }
            }

            if (onof) {
                cbOnOf.setChecked(true);
            } else {
                cbOnOf.setChecked(false);
            }

            txtLat.setText(marker.getPosition().latitude + "");
            txtLng.setText(marker.getPosition().longitude + "");
            return myContentsView;
        }
    }

    private void onOfChange() {
        friendOnOffs.clear();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FriendFragment.ONLINE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendOnOff friendOnOff = dataSnapshot.getValue(FriendOnOff.class);
                friendOnOffs.add(friendOnOff);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onOfChange();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                onOfChange();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                onOfChange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getScheduleFirebase() {
        if (scheduleItems == null) {
            scheduleItems = new ArrayList<>();
        } else {
            scheduleItems.clear();
        }
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(AddScheludeActivity.SCHEDULE).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ScheduleItem scheduleItem = dataSnapshot.getValue(ScheduleItem.class);
                scheduleItems.add(scheduleItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getScheduleFirebase();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getScheduleFirebase();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getScheduleFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getScheduleFirebase();
            }
        });
    }

    private void getFriendFirebase() {
        getScheduleFirebase();
        if (friends == null) {
            friends = new ArrayList<>();
        } else {
            friends.clear();
        }
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(FriendFragment1.FRIEND).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friends.add(friend);
                getLocationFriend();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getFriendFirebase();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getFriendFirebase();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getFriendFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getFriendFirebase();
            }

        });
    }

    private String linkMap(String lat1, String lng1, String lat2, String lng2) {
        String link = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        link = link + lat1 + "," + lng1;
        link = link + "&destination=" + lat2 + "," + lng2;
        link = link + "&key=AIzaSyDkgOCH8WCv0BborPb8NgQtyUpcnG1QFCI";
        return link;
    }

    private void sendRequest(String lat1, String lng1, String lat2, String lng2) {
        Object[] dataTransfer = new Object[6];
        GetDirectionsData getDirectionsData = new GetDirectionsData(getActivity());
        dataTransfer[0] = mMap;
        dataTransfer[1] = linkMap(lat1, lng1, lat2, lng2);
        dataTransfer[2] = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
        dataTransfer[3] = new LatLng(Double.parseDouble(lat2), Double.parseDouble(lng2));
        dataTransfer[4] = txtTime;
        dataTransfer[5] = txtDistance;
        getDirectionsData.execute(dataTransfer);
    }


    public void searchMap(String query) {
        showMakerFriend();
        List<Address> addressList;
        if (query != null || !query.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addressList = geocoder.getFromLocationName(query, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    final MarkerOptions markerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_3);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    markerOptions.position(latLng);
                    tempLatlng = latLng;
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(tempSchedule);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}