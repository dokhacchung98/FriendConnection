package com.example.admin.friendconnection.friend;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.action.UpdateUserActivity;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Admin on 4/21/2018.
 */

public class AddFriendFragment extends Fragment implements View.OnClickListener {
    View view;
    private FloatingActionButton flbtnAdd;
    private ListView lvFriend;
    private Dialog dialog;
    private ArrayList<String> addFriends;
    private DatabaseReference databaseReference;
    public static final String ADDFRIEND = "AddFriend";
    private String idMe;
    private SharedPreferences sharedPreferences;
    private AdapterAddFriend adapterAddFriend;
    private ArrayList<Friend> friends;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private final int RESULT_LOAD_IMG = 921;
    private ArrayList<Account> accounts;
    private TextView txtNodata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.framgent_add_friend, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(LoginFragment.FILE, Context.MODE_PRIVATE);
        friends = new ArrayList<>();
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        getArrFriend();
        accounts = new ArrayList<>();
        getAccount();
        txtNodata = view.findViewById(R.id.txtNoData1);
        lvFriend = view.findViewById(R.id.lvAddFriend);
        flbtnAdd = view.findViewById(R.id.flbtnAdd);
        flbtnAdd.setOnClickListener(this);
        addFriends = new ArrayList<>();
        adapterAddFriend = new AdapterAddFriend(getActivity(), addFriends, databaseReference, idMe);
        lvFriend.setAdapter(adapterAddFriend);
        getData();
        vibisilityText();
    }

    private void vibisilityText() {
        if (addFriends.size() == 0) {
            txtNodata.setVisibility(View.VISIBLE);
        } else {
            txtNodata.setVisibility(View.GONE);
        }
    }

    private void getData() {
        addFriends.clear();
        databaseReference.child(ADDFRIEND).child(idMe).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String friend = dataSnapshot.getValue().toString();
                addFriends.add(friend);
                vibisilityText();
                adapterAddFriend.notifyDataSetChanged();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flbtnAdd: {
                ActivityCompat.requestPermissions(getActivity(), new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                showDialogChoose();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showDialogChoose();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showDialogChoose() {
        dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_scan_qrcode_choose);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnCam = dialog.findViewById(R.id.btnChooseCamera);
        Button btnDire = dialog.findViewById(R.id.btnChooseDirectory);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanCameraActivity.class);
                startActivity(intent);
                dialog.cancel();
            }
        });
        btnDire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
                dialog.cancel();
            }
        });
        dialog.show();
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

    private void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == UpdateUserActivity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                InputStream imageStream = null;
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                send(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void send(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String id = result.getText();
        boolean check = checkQR(id);
//        boolean check1 = checkDuplicate(id);
        if (check) {
         /*   String push = databaseReference.push().getKey();
            AddFriend addFriend = new AddFriend(push, idMe);*/
            databaseReference.child(AddFriendFragment.ADDFRIEND).child(id).child(idMe).setValue(idMe, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(getContext(), "Error! An error occurred. Please try again later", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Is sending your friend request", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (!check) {
            Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
        } /*else if (!check1) {
            Toast.makeText(getContext(), "A friend invitation has been sent", Toast.LENGTH_SHORT).show();
        }*/
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
}