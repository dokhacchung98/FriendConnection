package com.example.admin.friendconnection.schedule;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.mylocation.GetDirectionsData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.admin.friendconnection.mylocation.MapFragment.REQUES_INT;

public class MapNotiFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private Bitmap smallMarkerSchedule;
    private NotiScheduleActivity notiScheduleActivity;
    private LatLng latLng;
    private Location myLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_noti, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        notiScheduleActivity = (NotiScheduleActivity) getActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latLng = notiScheduleActivity.getLatLng();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_2);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarkerSchedule = Bitmap.createScaledBitmap(b, 150, 150, false);
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
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = location;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(smallMarkerSchedule)));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (myLocation != null) {
                    Object[] dataTransfer = new Object[6];
                    GetGetDir getDirectionsData = new GetGetDir(getActivity());
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = linkMap(latLng.latitude + "", latLng.longitude + "", myLocation.getLatitude() + "", myLocation.getLongitude() + "");
                    dataTransfer[2] = new LatLng(latLng.latitude, latLng.longitude);
                    dataTransfer[3] = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    getDirectionsData.execute(dataTransfer);
                }
                return false;
            }
        });
        mMap.setMyLocationEnabled(true);
    }

    private String linkMap(String lat1, String lng1, String lat2, String lng2) {
        String link = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        link = link + lat1 + "," + lng1;
        link = link + "&destination=" + lat2 + "," + lng2;
        link = link + "&key=AIzaSyDkgOCH8WCv0BborPb8NgQtyUpcnG1QFCI";
        return link;
    }

    public class GetGetDir extends AsyncTask<Object, String, String> {
        private GoogleMap mMap;
        private String url;
        private LatLng startLatLng, endLatLng;


        HttpURLConnection httpURLConnection = null;
        String data = "";
        InputStream inputStream = null;
        Context c;
        private Polyline polyline;

        GetGetDir(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Object... params) {

            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            startLatLng = (LatLng) params[2];
            endLatLng = (LatLng) params[3];

            try {
                URL myurl = new URL(url);
                httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                bufferedReader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }


        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

                int count = jsonArray.length();
                String[] polyline_array = new String[count];

                JSONObject jsonobject2;

                for (int i = 0; i < count; i++) {
                    jsonobject2 = jsonArray.getJSONObject(i);

                    String polygone = jsonobject2.getJSONObject("polyline").getString("points");

                    polyline_array[i] = polygone;
                }

                int count2 = polyline_array.length;

                for (int i = 0; i < count2; i++) {
                    PolylineOptions options2 = new PolylineOptions();
                    options2.color(Color.parseColor("#FF4343"));
                    options2.width(6);
                    options2.addAll(PolyUtil.decode(polyline_array[i]));
                    mMap.addPolyline(options2);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
