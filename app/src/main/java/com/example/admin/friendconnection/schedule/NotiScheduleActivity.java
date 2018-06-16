package com.example.admin.friendconnection.schedule;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.mylocation.GetDirectionsData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotiScheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtTiele;
    private TextView txtTime;
    private TextView txtCalendar;
    private TextView txtValue;
    private TextView txtLocation;
    private MapView mMapView;
    private ScheduleItem scheduleItem;
    private String id;
    private LatLng latLng;
    private Location myLocation;
    private DatabaseReference databaseReference;
    private String idMe;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;
    private Button button;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_schedule);
        txtTiele = findViewById(R.id.txtTitle);
        txtCalendar = findViewById(R.id.txtCalendar);
        txtLocation = findViewById(R.id.txtLocation);
        txtTime = findViewById(R.id.txtTime);
        txtValue = findViewById(R.id.txtValue);
        button = findViewById(R.id.btnShow);
        button.setOnClickListener(this);
        Intent intent = getIntent();
        id = intent.getStringExtra(AddScheludeActivity.DATA);
        Log.e("RECIVER", "Nhận được id: " + id);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        databaseReference = FirebaseDatabase.getInstance().getReference(AddScheludeActivity.SCHEDULE);
        databaseReference.child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ScheduleItem sch = dataSnapshot.getValue(ScheduleItem.class);
                if (sch.getId().equals(id)) {
                    scheduleItem = sch;
                    init();
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

    }

    private void init() {
        latLng = new LatLng(Double.parseDouble(scheduleItem.getLat()), Double.parseDouble(scheduleItem.getLng()));
        txtValue.setText(scheduleItem.getValue());
        txtTime.setText(scheduleItem.getTime());
        txtLocation.setText(scheduleItem.getLocation());
        txtCalendar.setText(scheduleItem.getCalendar());
        txtTiele.setText(scheduleItem.getTitle());
        mMapView = findViewById(R.id.mapView);
    }

    private void showDialogMap(final LatLng latLng) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_map);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = dialog.findViewById(R.id.btnOkDialogMap);
        btnOk.setVisibility(View.GONE);
        Button btnCancle = dialog.findViewById(R.id.btnCancleDialogMap);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            }
        });

        SearchView searchView = dialog.findViewById(R.id.searchViewDialogMap);
        searchView.setVisibility(View.GONE);
        MapView mMapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);

        mMapView = dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(NotiScheduleActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(NotiScheduleActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                final MarkerOptions markerOptions = new MarkerOptions();
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_1);
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

    @Override
    public void onClick(View v) {
        showDialogMap(latLng);
    }
}
