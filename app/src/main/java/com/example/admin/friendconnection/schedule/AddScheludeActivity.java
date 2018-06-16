package com.example.admin.friendconnection.schedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddScheludeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnBack;
    private Button btnOk;
    private Button btnDelete;
    private Button btnCancle;
    private EditText txtTitle;
    private TextView txtTime;
    private TextView txtCalendar;
    private TextView txtLocation;
    private EditText txtValue;
    public static final String DATA = "senaa";
    private ScheduleItem scheduleItem;
    private boolean addNew = false;
    private View vSnackbar;
    private Snackbar snackbar;
    private DatabaseReference databaseReference;
    private String idMe;
    private SharedPreferences sharedPreferences;
    public static final String SCHEDULE = "Schedule";
    public static final String LOCA = "Location";
    public static final String LATTT = "Lat";
    public static final String LNGG = "Lng";
    private LatLng myLocation;
    private GoogleMap googleMap1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schelude);
        btnBack = findViewById(R.id.btnBackSchedule);
        btnOk = findViewById(R.id.btnok);
        btnCancle = findViewById(R.id.btncancle);
        btnDelete = findViewById(R.id.btndelete);
        txtTitle = findViewById(R.id.edtTitle);
        txtCalendar = findViewById(R.id.edtCalendar);
        txtTime = findViewById(R.id.edtTime);
        txtLocation = findViewById(R.id.edtLocation);
        txtValue = findViewById(R.id.edtValue);
        snackbar = Snackbar.make(txtTitle, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        scheduleItem = (ScheduleItem) intent.getSerializableExtra(DATA);
        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        if (scheduleItem == null) {
            addNew = true;
            addNewSchedule();
        } else {
            addNew = false;
            changeSchedule();
        }
    }

    private void changeSchedule() {
        myLocation = new LatLng(Double.parseDouble(scheduleItem.getLat()), Double.parseDouble(scheduleItem.getLng()));
        txtValue.setEnabled(false);
        txtTitle.setEnabled(false);
        txtTitle.setText(scheduleItem.getTitle());
        txtValue.setText(scheduleItem.getValue());
        txtLocation.setText(scheduleItem.getLocation());
        txtTime.setText(scheduleItem.getTime());
        txtCalendar.setText(scheduleItem.getCalendar());
        btnOk.setText("Change");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToChange();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(Double.parseDouble(scheduleItem.getLat()), Double.parseDouble(scheduleItem.getLng()));
                showDialogMap(latLng);
            }
        });
    }

    private void deleteScheDule() {
        databaseReference.child(SCHEDULE).child(idMe).child(scheduleItem.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showSnackbar(R.color.colorSuccess, "Delete schedule successfully", R.drawable.ic_check_black_24dp);
                    finish();
                } else {
                    showSnackbar(R.color.colorError, "Delete schedule error", R.drawable.ic_close_black_24dp);
                }
            }
        });
    }

    private void setToChange() {
        txtTitle.setEnabled(true);
        txtValue.setEnabled(true);
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTime();
            }
        });
        txtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCalendar();
            }
        });
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMap(null);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeToChange();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void codeToChange() {
        String title = txtTitle.getText().toString().trim();
        String time = txtTime.getText().toString().trim();
        String calendar = txtCalendar.getText().toString().trim();
        String lat = "";
        String lng = "";
        String location = txtLocation.getText().toString();
        String value = txtValue.getText().toString().trim();
        if (title.isEmpty() || time.isEmpty() || calendar.isEmpty() || myLocation == null || location.isEmpty() || value.isEmpty()) {
            showSnackbar(R.color.colorError, "Please complete all information", R.drawable.ic_close_black_24dp);
            return;
        }
        lat = myLocation.latitude + "";
        lng = myLocation.longitude + "";
        ScheduleItem temp = new ScheduleItem(scheduleItem.getId(), title, value, time, calendar, location, lat, lng);
        databaseReference.child(SCHEDULE).child(idMe).child(scheduleItem.getId()).setValue(temp, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showSnackbar(R.color.colorSuccess, "Add scheduling success", R.drawable.ic_check_black_24dp);
                    finish();
                } else {
                    showSnackbar(R.color.colorError, "Add scheduling error", R.drawable.ic_close_black_24dp);
                }
            }
        });
    }

    private void showDialogTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.MyTimePickerDialogStyle,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String h = hourOfDay + "";
                        if (hourOfDay < 10) {
                            h = "0" + h;
                        }
                        String m = minute + "";
                        if (minute < 10) {
                            m = "0" + m;
                        }
                        txtTime.setText(h + ":" + m);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void showDialogCalendar() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.MyTimePickerDialogStyle,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String n = dayOfMonth + "";
                        if (dayOfMonth < 10) {
                            n = "0" + n;
                        }
                        String t = (month + 1) + "";
                        if (month + 1 < 10) {
                            t = "0" + t;
                        }
                        txtCalendar.setText(n + "-" + t + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showDialogMap(final LatLng latLng) {
        final Dialog dialog = new Dialog(AddScheludeActivity.this);
        dialog.setContentView(R.layout.dialog_choose_map);
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = dialog.findViewById(R.id.btnOkDialogMap);
        Button btnCancle = dialog.findViewById(R.id.btnCancleDialogMap);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
                txtValue.setText("");
                myLocation = null;
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });


        SearchView searchView = dialog.findViewById(R.id.searchViewDialogMap);

        MapView mMapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);

        mMapView = dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap1 = googleMap;
                if (ActivityCompat.checkSelfPermission(AddScheludeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(AddScheludeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                final MarkerOptions markerOptions = new MarkerOptions();
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_2);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                if (latLng != null) {
                    markerOptions.position(latLng);
                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        myLocation = latLng;
                        googleMap.clear();
                        markerOptions.position(latLng);
                        googleMap.addMarker(markerOptions);
                        getNameAddress(latLng);
                    }
                });
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onMapSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if (!dialog.isShowing()) {
            //clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            dialog.show();
        }
    }

    private void showDialogDelete() {
        final Dialog dialog = new Dialog(AddScheludeActivity.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        Button btnCancle = dialog.findViewById(R.id.btnCancleLogout);
        Button btnYes = dialog.findViewById(R.id.btnYesLogout);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteScheDule();
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void onMapSearch(String query) {
        List<Address> addressList = new ArrayList<>();
        if (query != null || !query.equals("")) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocationName(query, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    final MarkerOptions markerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location_2);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    markerOptions.position(latLng);
                    googleMap1.addMarker(markerOptions);
                    googleMap1.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
                    txtLocation.setText(address.getAddressLine(0));
                    myLocation = latLng;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getNameAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            txtLocation.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNewSchedule() {
        Intent intent = getIntent();
        String laa = intent.getStringExtra(LATTT);
        String lnn = intent.getStringExtra(LNGG);
        String po = intent.getStringExtra(LOCA);

        if (po != null && laa != null && lnn != null) {
            Double latt = Double.parseDouble(laa);
            Double lngg = Double.parseDouble(lnn);
            txtLocation.setText(po);
            myLocation = new LatLng(latt, lngg);
        }
        txtValue.setEnabled(true);
        txtTitle.setEnabled(true);
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTime();
            }
        });
        txtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCalendar();
            }
        });
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMap(null);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToAddSchedule();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDelete.setVisibility(View.GONE);
    }

    private void checkToAddSchedule() {
        String title = txtTitle.getText().toString().trim();
        String time = txtTime.getText().toString().trim();
        String calendar = txtCalendar.getText().toString().trim();
        String lat = "";
        String lng = "";
        String location = txtLocation.getText().toString();
        String value = txtValue.getText().toString().trim();
        if (title.isEmpty() || time.isEmpty() || calendar.isEmpty() || myLocation == null || location.isEmpty() || value.isEmpty()) {
            showSnackbar(R.color.colorError, "Please complete all information", R.drawable.ic_close_black_24dp);
            return;
        }
        lat = myLocation.latitude + "";
        lng = myLocation.longitude + "";
        String push = databaseReference.push().getKey();
        ScheduleItem temp = new ScheduleItem(push, title, value, time, calendar, location, lat, lng);
        databaseReference.child(SCHEDULE).child(idMe).child(push).setValue(temp, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showSnackbar(R.color.colorSuccess, "Add scheduling success", R.drawable.ic_check_black_24dp);
                    finish();
                } else {
                    showSnackbar(R.color.colorError, "Add scheduling error", R.drawable.ic_close_black_24dp);
                }
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    private void showSnackbar(int color, String result, int icon) {
        vSnackbar.setBackgroundColor(ContextCompat.getColor(this, color));
        snackbar.setActionTextColor(R.color.colorWhite);
        snackbar.setText("\t" + result);
        TextView textView = vSnackbar.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btncancle) {
            AddScheludeActivity.this.finish();
        } else if (v.getId() == R.id.btndelete) {
            showDialogDelete();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
