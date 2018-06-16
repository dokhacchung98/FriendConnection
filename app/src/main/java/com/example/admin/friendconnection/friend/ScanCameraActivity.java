package com.example.admin.friendconnection.friend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScanCameraActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    final int REQUES_CODE = 1210;
    private TextView textView;
    private String idMe;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private ArrayList<Friend> friends;
    private ArrayList<Account> accounts;
    private ArrayList<AddFriend> addFriends;
    private boolean checka = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_camera);
        surfaceView = findViewById(R.id.surface);
        textView = findViewById(R.id.txtIdScanCam);
        friends = new ArrayList<>();
        progressDialog = new ProgressDialog(ScanCameraActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Is processing data");

        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getArrFriend();
        accounts = new ArrayList<>();
        addFriends = new ArrayList<>();
        getAccount();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanCameraActivity.this, new String[]{Manifest.permission.CAMERA}, REQUES_CODE);
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if (barcodeSparseArray.size() != 0 && checka) {
                    checka = false;
                    final String value = barcodeSparseArray.valueAt(0).displayValue;
                    boolean check = checkQR(value);
                    //  boolean check1 = checkDuplicate(value);
                    if (check) {
                        for (int i = 0; i < friends.size(); i++) {
                            if (value.equals(friends.get(i).getPerson())) {
                                Toast.makeText(ScanCameraActivity.this, "This person is already a friend", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                     /*   String push = databaseReference.push().getKey();
                        AddFriend addFriend = new AddFriend(push, idMe);*/
                        databaseReference.child(AddFriendFragment.ADDFRIEND).child(value).child(idMe).setValue(idMe, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(ScanCameraActivity.this, "Error! An error occurred. Please try again later", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ScanCameraActivity.this, "Is sending your friend request", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });
                    } else if (!check) {
                        Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    } /*else if (!check1) {
                        Toast.makeText(getApplicationContext(), "A friend invitation has been sent", Toast.LENGTH_SHORT).show();
                    }*/
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            textView.setText(value);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUES_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void getArrFriend() {
        friends.clear();
        databaseReference.child(FriendFragment1.FRIEND).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friends.add(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getArrFriend();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getArrFriend();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getArrFriend();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getArrFriend();
            }
        });
    }

    private boolean checkQR(String value) {
        if (value.equals(idMe)) {
            return false;
        }
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void getAccount() {
        accounts.clear();
        databaseReference.child("Account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account account = dataSnapshot.getValue(Account.class);
                accounts.add(account);
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
                getAccount();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
