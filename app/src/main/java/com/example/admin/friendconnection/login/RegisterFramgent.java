package com.example.admin.friendconnection.login;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.friend.FriendFragment;
import com.example.admin.friendconnection.friend.FriendOnOff;
import com.example.admin.friendconnection.model.ConvertPass;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Admin on 4/9/2018.
 */

public class RegisterFramgent extends Fragment implements View.OnClickListener {
    private View view;
    private EditText edtUser;
    private EditText edtPass1;
    private EditText edtPass2;
    private Button btnHaveAccount;
    private Button btnRes;
    private LoginActivity activity;
    private DatabaseReference databaseReference;
    private float x, lastX;
    private Snackbar snackbar;
    private View vSnackbar;
    private ArrayList<Account> accounts;
    private ConvertPass convertPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (LoginActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_register, container, false);
        accounts = new ArrayList<>();
        accounts = activity.getAccounts();
        convertPass = new ConvertPass();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();
        edtUser = activity.findViewById(R.id.edtUserRes);
        snackbar = Snackbar.make(edtUser, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        edtPass1 = activity.findViewById(R.id.edtPassRes1);
        edtPass2 = activity.findViewById(R.id.edtPassRes2);
        btnHaveAccount = activity.findViewById(R.id.btnHaveAccount);
        btnRes = activity.findViewById(R.id.btnRes);
        btnRes.setOnClickListener(this);
        btnHaveAccount.setOnClickListener(this);
        //        view.setOnTouchListener(new View.OnTouchListener() {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                switch (event.getAction()) {
        //                    case MotionEvent.ACTION_DOWN: {
        //                        x = event.getX();
        //                        break;
        //                    }
        //                    case MotionEvent.ACTION_MOVE: {
        //                        lastX = event.getRawX();
        //                        break;
        //                    }
        //                    case MotionEvent.ACTION_UP: {
        //                        if (lastX - x > 300) {
        //                            Toast.makeText(activity, "trai", Toast.LENGTH_SHORT).show();
        //                        } else if (lastX - x < -300) {
        //                            Toast.makeText(activity, "Phai", Toast.LENGTH_SHORT).show();
        //                        }
        //                        break;
        //                    }
        //                }
        //                return true;
        //            }
        //        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnRes) {
            final String user = edtUser.getText().toString().trim();
            final String pass = edtPass1.getText().toString().trim();
            String passConvert = edtPass2.getText().toString().trim();

            if (user.isEmpty()) {
                showSnackbar(R.color.colorError, "Please enter your Username", R.drawable.ic_close_black_24dp);
            } else if (pass.isEmpty()) {
                showSnackbar(R.color.colorError, "Please enter your Password", R.drawable.ic_close_black_24dp);
            } else if (passConvert.isEmpty()) {
                showSnackbar(R.color.colorError, "Enter check Password", R.drawable.ic_close_black_24dp);
                edtPass2.setText("");
            } else if (!pass.equals(passConvert)) {
                showSnackbar(R.color.colorError, "Passwords do not match", R.drawable.ic_close_black_24dp);
                edtPass2.setText("");
            } else if (pass.length() < 6) {
                showSnackbar(R.color.colorError, "Password must be 6 characters", R.drawable.ic_close_black_24dp);
                edtPass2.setText("");
            } else {
                if (activity.isNetworkConnected()) {
                    if (checkCoincideAccount(user)) {
                        showSnackbar(R.color.colorError, "Account already exists", R.drawable.ic_close_black_24dp);
                        edtUser.setText("");
                        edtPass1.setText("");
                        edtPass2.setText("");
                    } else {
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        final String id = databaseReference.push().getKey();
                        Log.e("Id   :", id);
                        String passCV = convertPass.convert(pass);
                        final Account account = new Account(user, passCV, "", "", id, "", "", "", "");
                        activity.isLoading();
                        databaseReference.child("Account").child(id).setValue(account, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    vSnackbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorError));
                                    snackbar.setActionTextColor(R.color.colorWhite);
                                    snackbar.setText("\t" + databaseError);
                                    TextView textView = vSnackbar.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.stat_notify_error, 0, 0, 0);
                                    snackbar.show();
                                } else {
                                    createUserOnOf(id);
                                    showSnackbar(R.color.colorSuccess, "Create account success", R.drawable.ic_check_black_24dp);
                                    activity.setUserPass(user, pass);
                                    activity.showFragment(activity.getLoginFragment());
                                    edtPass1.setText("");
                                    edtPass2.setText("");
                                    edtUser.setText("");
                                }
                                activity.isLoadingFinish();
                            }
                        });
                    }
                } else {
                    showSnackbar(R.color.colorError, "No network connection", R.drawable.ic_close_black_24dp);
                }
            }
        } else if (v.getId() == R.id.btnHaveAccount) {
            activity.showFragment(activity.getLoginFragment());
        }
    }

    private void createUserOnOf(String id) {
        FriendOnOff friendOnOff = new FriendOnOff();
        friendOnOff.setId(id);
        friendOnOff.setOnof(FriendFragment.OFFLINE);
        databaseReference.child(FriendFragment.ONLINE).child(id).setValue(friendOnOff);
    }

    private boolean checkCoincideAccount(String user) {
        for (int i = 0; i < accounts.size(); i++) {
            if (user.equals(accounts.get(i).getUserName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("ResourceAsColor")
    private void showSnackbar(int color, String result, int icon) {
        vSnackbar.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snackbar.setActionTextColor(R.color.colorWhite);
        snackbar.setText("\t" + result);
        TextView textView = vSnackbar.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        snackbar.show();
    }
}
