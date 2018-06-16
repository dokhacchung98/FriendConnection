package com.example.admin.friendconnection.action;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.home.ActivityHome;
import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.model.ConvertName;
import com.example.admin.friendconnection.object.Account;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Admin on 4/15/2018.
 */

public class InforUserActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private View vSnackbar;
    private Snackbar snackbar;
    private ImageView imgAva;
    private ImageView imgChoose;
    private EditText edtName;
    private EditText edtPhone;
    private EditText edtBirthDay;
    private EditText edtMail;
    private TextView txtUser;
    private Button btnConfirm;
    private String name;
    private String mail;
    private String phone;
    private String birthDay;
    private String sex = "Male";
    private String id = "";
    private String linkAva;
    private DatabaseReference databaseReference;
    public static final String KEYINTENT = "keyintent";
    private Account account;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private long timePressBack;
    private int RESULT_LOAD_IMG = 121;
    private SpinKitView spinKitView;
    private ConvertName convertName;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_user);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        convertName = new ConvertName();
        Intent intent = getIntent();
        id = intent.getStringExtra(KEYINTENT);
        account = new Account();
        if (id != null) {
            databaseReference.child("Account").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    account.setUserName(dataSnapshot.child("userName").getValue().toString());
                    account.setPassWord(dataSnapshot.child("passWord").getValue().toString());
                    account.setId(id);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        radioButton1 = findViewById(R.id.radio1);
        radioButton2 = findViewById(R.id.radio2);
        imgAva = findViewById(R.id.imgAva);
        imgChoose = findViewById(R.id.imgChoose);
        edtName = findViewById(R.id.edtName);
        edtBirthDay = findViewById(R.id.edtBirthDay);
        edtPhone = findViewById(R.id.edtPhone);
        edtMail = findViewById(R.id.edtMail);
        btnConfirm = findViewById(R.id.btnConfirm);
        spinKitView = findViewById(R.id.spin_kit);
        btnConfirm.setOnClickListener(this);
        imgChoose.setOnClickListener(this);
        radioButton1.setOnCheckedChangeListener(this);
        radioButton2.setOnCheckedChangeListener(this);
        snackbar = Snackbar.make(radioButton1, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        txtUser = findViewById(R.id.txtUsser);
        txtUser.setText(account.getUserName());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.radio1 && isChecked) {
            sex = "Male";
        } else if (buttonView.getId() == R.id.radio2 && isChecked) {
            sex = "Female";
        }
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
        if (v.getId() == R.id.btnConfirm) {
            name = edtName.getText().toString().trim();
            name = convertName.formatName(name);
            mail = edtMail.getText().toString().trim();
            phone = edtPhone.getText().toString().trim();
            birthDay = edtBirthDay.getText().toString().trim();
            account.setName(name);
            account.setBirthDay(birthDay);
            account.setMail(mail);
            account.setPhone(phone);
            account.setSex(sex);
            if (!id.isEmpty() && !name.isEmpty() && !mail.isEmpty() && !phone.isEmpty() && !birthDay.isEmpty()) {
                spinKitView.setVisibility(View.VISIBLE);
                updateImage();
            } else {
                showSnackbar(R.color.colorError, "Must not be empty", R.drawable.ic_close_black_24dp);
            }
        } else if (v.getId() == R.id.imgChoose) {
            //chooseAvatar();
            getImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgAva.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(InforUserActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(InforUserActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void updateImage() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReferenceFromUrl("gs://ban-moi.appspot.com");
        StorageReference mountainsRef = storageRef.child(id + ".png");
        imgAva.setDrawingCacheEnabled(true);
        imgAva.buildDrawingCache();
        Bitmap bitmap = imgAva.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmapResize = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, true);
        bitmapResize.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
//        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length).
//                profileImage.setImageBitmap(Bitmap.createScaledBitmap(data, 120, 120, false));
        // Bitmap bmNew=Bitmap.createBitmap()

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                showSnackbar(R.color.colorError, "Upload Avatar Error", R.drawable.ic_close_black_24dp);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkAva = downloadUrl.toString();
                /**Luu gia tri ava va id*/
                SharedPreferences sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LoginFragment.LINK, linkAva);
                editor.putString(LoginFragment.ID, id);
                editor.putString(LoginFragment.NAME, name);
                editor.putString(LoginFragment.MAIL, mail);
                editor.putString(LoginFragment.PHONE, phone);
                editor.putString(LoginFragment.SEX, sex);
                editor.putString(LoginFragment.BIRTH, birthDay);
                editor.commit();

                Log.e("Link image: ", linkAva);
                account.setLinkAvatar(linkAva);
                databaseReference.child("Account").child(id).setValue(account, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        spinKitView.setVisibility(View.GONE);
                        showSnackbar(R.color.colorSuccess, "Update successful", R.drawable.ic_check_black_24dp);
                        Intent intent = new Intent(InforUserActivity.this, ActivityHome.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_bounce, 0);
                        InforUserActivity.this.finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (timePressBack + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        timePressBack = System.currentTimeMillis();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}