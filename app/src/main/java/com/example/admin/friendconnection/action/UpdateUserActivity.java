package com.example.admin.friendconnection.action;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.model.ConvertName;
import com.example.admin.friendconnection.model.ConvertPass;
import com.example.admin.friendconnection.object.Account;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private RadioButton radioButton1;
    private RadioButton radioButton2;
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
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private int RESULT_LOAD_IMG = 121;
    private boolean changeAva = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View vSnackbar;
    private Snackbar snackbar;
    private Account account;
    private Button btnCanle;
    private String user;
    private String pass;
    private ConvertPass convertPass;
    private SpinKitView spinKitView;
    private ConvertName convertName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        radioButton1 = findViewById(R.id.radio1);
        radioButton2 = findViewById(R.id.radio2);
        imgAva = findViewById(R.id.imgAva);
        imgChoose = findViewById(R.id.imgChoose);
        edtName = findViewById(R.id.edtName);
        edtBirthDay = findViewById(R.id.edtBirthDay);
        edtPhone = findViewById(R.id.edtPhone);
        edtMail = findViewById(R.id.edtMail);
        btnConfirm = findViewById(R.id.btnUpdateUser);
        btnCanle = findViewById(R.id.btnCancleUpdate);
        txtUser = findViewById(R.id.txtUsser);
        spinKitView = findViewById(R.id.spin_kit);
        convertName = new ConvertName();
        account = new Account();
        txtUser.setText(sharedPreferences.getString(LoginFragment.USER, ""));
        edtName.setText(sharedPreferences.getString(LoginFragment.NAME, ""));
        edtMail.setText(sharedPreferences.getString(LoginFragment.MAIL, ""));
        edtPhone.setText(sharedPreferences.getString(LoginFragment.PHONE, ""));
        edtBirthDay.setText(sharedPreferences.getString(LoginFragment.BIRTH, ""));
        sex = sharedPreferences.getString(LoginFragment.SEX, "Male");
        linkAva = sharedPreferences.getString(LoginFragment.LINK, "");
        id = sharedPreferences.getString(LoginFragment.ID, "");
        user = sharedPreferences.getString(LoginFragment.USER, "");
        pass = sharedPreferences.getString(LoginFragment.PASS, "");
        Picasso.get().load(linkAva).placeholder(R.drawable.user).into(imgAva);
        if (sex.equals("Male")) {
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
        } else {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
        }
        btnCanle.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        imgChoose.setOnClickListener(this);
        radioButton1.setOnCheckedChangeListener(this);
        radioButton2.setOnCheckedChangeListener(this);
        snackbar = Snackbar.make(radioButton1, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        convertPass = new ConvertPass();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdateUser) {
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
            account.setId(id);
            account.setPassWord(convertPass.convert(pass));
            account.setUserName(user);
            if (!name.isEmpty() && !mail.isEmpty() && !phone.isEmpty() && !birthDay.isEmpty()) {
                spinKitView.setVisibility(View.VISIBLE);
                if (changeAva) {
                    updateImage();
                } else {
                    update();
                }
            } else {
                showSnackbar(R.color.colorError, "Must not be empty", R.drawable.ic_close_black_24dp);
            }
        } else if (v.getId() == R.id.imgChoose) {
            getImage();
        } else if (v.getId() == R.id.btnCancleUpdate) {
            UpdateUserActivity.this.finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                InputStream imageStream = null;
                imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgAva.setImageBitmap(selectedImage);
                changeAva = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(UpdateUserActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(UpdateUserActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            changeAva = false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.radio1 && isChecked) {
            sex = "Male";
        } else if (buttonView.getId() == R.id.radio2 && isChecked) {
            sex = "Female";
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
        Bitmap bitmapResize = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2,
                bitmap.getHeight() * 2, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapResize.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                showSnackbar(R.color.colorError, "Upload Avatar Error", R.drawable.ic_close_black_24dp);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                changeAva = false;
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkAva = downloadUrl.toString();
                /**Luu gia tri ava va id*/
                update();
            }
        });
    }

    private void update() {
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
                showSnackbar(R.color.colorSuccess, "Update successful", R.drawable.ic_check_black_24dp);
                spinKitView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
