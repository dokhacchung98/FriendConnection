package com.example.admin.friendconnection.action;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.friendconnection.home.ActivityHome;
import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.friend.Friend;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private CircleImageView imgAva;
    private TextView txtUser;
    private TextView txtName;
    private TextView txtSex;
    private TextView txtPhone;
    private TextView txtMail;
    private TextView txtBirth;
    private Button btnQr;
    private Button btnOk;
    private Button btnUpdate;
    private Button btnChangePass;
    private SharedPreferences sharedPreferences;
    private String id;
    private DatabaseReference databaseReference;
    private Account account = new Account();
    private String idMe;
    private Dialog dialog;
    private Bitmap bitmapShare;
    private Dialog dialogChange;
    private LinearLayout lnAllow;
    private TextView txtAllow;
    private SwitchCompat switchAllow;
    private Friend friendMe;
    private View vSnackbar;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        Intent intent = getIntent();
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        id = intent.getStringExtra(LoginFragment.ID);
        Log.e("Id me", idMe + " --- " + id);
        if (id.isEmpty()) {
            id = idMe;
        }
        imgAva = findViewById(R.id.imgAvaInfor);
        txtUser = findViewById(R.id.txtUserInfor);
        txtName = findViewById(R.id.txtNameInfor);
        txtSex = findViewById(R.id.txtSexInfor);
        txtPhone = findViewById(R.id.txtPhoneInfor);
        txtMail = findViewById(R.id.txtMailInfor);
        txtBirth = findViewById(R.id.txtBirthInfor);
        btnOk = findViewById(R.id.btnOkInfor);
        btnQr = findViewById(R.id.btnQrInfor);
        btnUpdate = findViewById(R.id.btnUpdateInfor);
        btnChangePass = findViewById(R.id.btnChangePass);
        lnAllow = findViewById(R.id.lnAllow);
        txtAllow = findViewById(R.id.txtAllow);
        switchAllow = findViewById(R.id.switchAllow);
        switchAllow.setOnCheckedChangeListener(this);
        btnUpdate.setOnClickListener(this);
        btnChangePass.setOnClickListener(this);
        btnQr.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        snackbar = Snackbar.make(switchAllow, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (!id.equals(idMe)) {
            btnChangePass.setVisibility(View.GONE);
            btnQr.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            txtUser.setVisibility(View.GONE);
            lnAllow.setVisibility(View.VISIBLE);
            getAllow();
        } else {
            lnAllow.setVisibility(View.GONE);
            txtUser.setVisibility(View.VISIBLE);
        }
        databaseReference.child(ActivityHome.ACCOUNT).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account = dataSnapshot.getValue(Account.class);
                showInfor();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showInfor() {
        Picasso.get().load(account.getLinkAvatar()).placeholder(R.drawable.user).into(imgAva);
        txtUser.setText(account.getUserName());
        txtName.setText(account.getName());
        txtSex.setText(account.getSex());
        txtPhone.setText(account.getPhone());
        txtMail.setText(account.getMail());
        txtBirth.setText(account.getBirthDay());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQrInfor: {
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_qrcode);
                ImageView imgQr = dialog.findViewById(R.id.imtQrcodeDialog);
                Button btnClode = dialog.findViewById(R.id.btnCloseDialogQrcode);
                Button btnShareQr = dialog.findViewById(R.id.btnShareQrcode);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 250, 250);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    bitmapShare = bitmap;
                    imgQr.setImageBitmap(bitmapShare);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                btnClode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btnShareQr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveImage();
                    }
                });
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
                break;
            }
            case R.id.btnUpdateInfor: {
                Intent intent = new Intent(UserActivity.this, UpdateUserActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            }
            case R.id.btnChangePass: {
                showDialogChangePass();
                break;
            }
            case R.id.btnOkInfor: {
                this.finish();
                break;
            }
        }
    }

    private void getAllow() {
        databaseReference.child(FriendFragment.FRIEND).child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                if (friend.getPerson().equals(idMe)) {
                    friendMe = friend;
                    showSwitch();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAllow();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getAllow();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getAllow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getAllow();
            }
        });
    }

    private void showSwitch() {
        if (friendMe.getMode().equals(FriendFragment.ALLOW)) {
            switchAllow.setChecked(true);
        } else {
            switchAllow.setChecked(false);
        }
    }

    private void showDialogChangePass() {
        dialogChange = new Dialog(UserActivity.this);
        dialogChange.setContentView(R.layout.dialog_change_pass);
        dialogChange.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText edtNew = dialogChange.findViewById(R.id.edtPass1);
        final EditText edtOld = dialogChange.findViewById(R.id.edtPassOld);
        final EditText edtAgain = dialogChange.findViewById(R.id.edtPass2);
        Button btnChange = dialogChange.findViewById(R.id.btnChange);
        Button btnCancel = dialogChange.findViewById(R.id.btnCancle);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = edtOld.getText().toString().trim();
                String newP = edtNew.getText().toString().trim();
                String again = edtAgain.getText().toString().trim();
                if (old.isEmpty() || newP.isEmpty() || again.isEmpty()) {
                    Toast.makeText(UserActivity.this, "Please complete all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (!checkPass(old)) {
                        Toast.makeText(UserActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                    } else {
                        if (newP.equals(again)) {
                            account.setPassWord(newP);
                            updatePass();
                        } else {
                            Toast.makeText(UserActivity.this, "Passwords are not identical", Toast.LENGTH_SHORT).show();
                            edtAgain.setText("");
                            edtNew.setText("");
                        }
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChange.cancel();
            }
        });
        dialogChange.show();
    }

    private void updatePass() {
        databaseReference.child("Account").child(idMe).setValue(account, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(UserActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                dialogChange.cancel();
            }
        });
    }

    private boolean checkPass(String old) {
        if (account.getPassWord().equals(old)) {
            return true;
        }
        return false;
    }

    private void saveImage() {
        try {
            File file = new File(this.getExternalCacheDir(), "filesend.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmapShare.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share QRCode For Friend"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String mode;
        if (isChecked) {
            mode = FriendFragment.ALLOW;
        } else {
            mode = FriendFragment.UNALLOW;
        }
        databaseReference.child(FriendFragment.FRIEND).child(id).child(friendMe.getId()).child("mode").setValue(mode, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showSnackbar(R.color.colorSuccess, "Success", R.drawable.ic_check_black_24dp);
                } else {
                    showSnackbar(R.color.colorError, "Error", R.drawable.ic_check_black_24dp);
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
