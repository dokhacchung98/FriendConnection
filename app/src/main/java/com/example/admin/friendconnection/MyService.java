package com.example.admin.friendconnection;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.model.ConvertPass;
import com.example.admin.friendconnection.mylocation.LocationItem;
import com.example.admin.friendconnection.mylocation.PutGetLocation;
import com.example.admin.friendconnection.object.Account;
import com.example.admin.friendconnection.schedule.AddScheludeActivity;
import com.example.admin.friendconnection.schedule.NotiScheduleActivity;
import com.example.admin.friendconnection.schedule.ScheduleItem;
import com.example.admin.friendconnection.schedule.ScheduleReciver;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.maps.zzac;
import com.google.android.gms.internal.maps.zzk;
import com.google.android.gms.internal.maps.zzn;
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.internal.maps.zzw;
import com.google.android.gms.internal.maps.zzz;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.ILocationSourceDelegate;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.internal.zzab;
import com.google.android.gms.maps.internal.zzad;
import com.google.android.gms.maps.internal.zzaf;
import com.google.android.gms.maps.internal.zzaj;
import com.google.android.gms.maps.internal.zzal;
import com.google.android.gms.maps.internal.zzan;
import com.google.android.gms.maps.internal.zzap;
import com.google.android.gms.maps.internal.zzar;
import com.google.android.gms.maps.internal.zzat;
import com.google.android.gms.maps.internal.zzav;
import com.google.android.gms.maps.internal.zzax;
import com.google.android.gms.maps.internal.zzaz;
import com.google.android.gms.maps.internal.zzbb;
import com.google.android.gms.maps.internal.zzbd;
import com.google.android.gms.maps.internal.zzbf;
import com.google.android.gms.maps.internal.zzbs;
import com.google.android.gms.maps.internal.zzc;
import com.google.android.gms.maps.internal.zzh;
import com.google.android.gms.maps.internal.zzl;
import com.google.android.gms.maps.internal.zzp;
import com.google.android.gms.maps.internal.zzr;
import com.google.android.gms.maps.internal.zzv;
import com.google.android.gms.maps.internal.zzx;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyService extends Service {
    private static final String TAG = "Service";
    private DatabaseReference databaseReference;
    private GoogleMap mMap;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String idMe;
    private String passOld;
    private ArrayList<ScheduleItem> scheduleItems;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SERVICE", "Start command is running");

        return START_REDELIVER_INTENT; //START_REDELIVER_INTENT
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SERVICE", "Oncreate running");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(PutGetLocation.LOCATION);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        passOld = sharedPreferences.getString(LoginFragment.PASS, "");
        getSchedule();
        //checkAccountChange();
        Log.e("TIME", "id: " + idMe);
        initializeLocationManager();
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void checkAccountChange() {
//        databaseReference.child("Account").child(idMe).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Account account = dataSnapshot.getValue(Account.class);
//                if (account.getId().equals(idMe)) {
//                    ConvertPass convertPass = new ConvertPass();
//                    String pass = account.getPassWord();
//                    pass = convertPass.deConvert(pass);
//                    if(!pass.equals(passOld))
//                    editor.putString(LoginFragment.PASS, pass);
//                    editor.commit();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        })
    }

    @Override
    public void onDestroy() {
//        Log.e("Service", "Service is destroy");
        super.onDestroy();
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            //  Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
//            Log.e(TAG, "onLocationChanged: " + location);
            // Log.e(TAG, "Vị trí hiện tại " + location.getLongitude() + " - " + location.getLatitude());
            sendlocationMe(location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void sendlocationMe(Location location) {
        LocationItem locationItem = new LocationItem(idMe, location.getLatitude() + "", location.getLongitude() + "");
        databaseReference.child(PutGetLocation.LOCATION).child(idMe).setValue(locationItem);
    }

    private void getSchedule() {
        if (scheduleItems == null) {
            scheduleItems = new ArrayList<>();
        } else {
            scheduleItems.clear();
        }
        databaseReference.child(AddScheludeActivity.SCHEDULE).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ScheduleItem scheduleItem = dataSnapshot.getValue(ScheduleItem.class);
                scheduleItems.add(scheduleItem);
                //clearSchedule(scheduleItem);
                setUpAlarm();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getSchedule();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getSchedule();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getSchedule();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getSchedule();
            }
        });
    }

    private void clearSchedule(ScheduleItem scheduleItem) {
        Date t1 = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(t1);
        String a[] = scheduleItem.getCalendar().split("-");
        String b[] = scheduleItem.getTime().split(":");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, Integer.parseInt(a[2]));
        ca.set(Calendar.DATE, Integer.parseInt(a[0]));
        ca.set(Calendar.MONTH, Integer.parseInt(a[1]) - 1);
        ca.set(Calendar.HOUR_OF_DAY, Integer.parseInt(b[0]));
        ca.set(Calendar.MINUTE, Integer.parseInt(b[1]));

        Date t2 = ca.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(t2);
        calendar1.set(Calendar.YEAR, 2018);
        if (t1.compareTo(t2) > 0) {
            databaseReference.child(AddScheludeActivity.SCHEDULE).child(idMe).child(scheduleItem.getId()).removeValue();
        }
    }

    private boolean setUp(String date, String cal) {
        Date t1 = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(t1);
        String a[] = cal.split("-");
        String b[] = date.split(":");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, Integer.parseInt(a[2]));
        ca.set(Calendar.DATE, Integer.parseInt(a[0]));
        ca.set(Calendar.MONTH, Integer.parseInt(a[1]) - 1);
        ca.set(Calendar.HOUR_OF_DAY, Integer.parseInt(b[0]));
        ca.set(Calendar.MINUTE, Integer.parseInt(b[1]));

        Date t2 = ca.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(t2);
        calendar1.set(Calendar.YEAR, 2018);
        if (t1.compareTo(t2) >= 0) {
            return false;
        }
        return true;
    }


    private void setUpAlarm() {
        if (scheduleItems.size() > 0) {
            for (int i = 0; i < scheduleItems.size(); i++) {
                String a[] = scheduleItems.get(i).getTime().split(":");
                String b[] = scheduleItems.get(i).getCalendar().split("-");
                if (setUp(scheduleItems.get(i).getTime(), scheduleItems.get(i).getCalendar())) {
                    Log.e("TIME", a[0] + "  " + a[1] + " " + b[0] + " " + b[1] + " " + b[2]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(b[2]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(b[1]) - 1);
                    calendar.set(Calendar.DATE, Integer.parseInt(b[0]));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(a[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(a[1]));
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(this, ScheduleReciver.class);
                    intent.putExtra(AddScheludeActivity.DATA, scheduleItems.get(i).getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}


