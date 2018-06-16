package com.example.admin.friendconnection.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.friendconnection.home.ActivityHome;
import com.example.admin.friendconnection.MyService;
import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.action.InforUserActivity;
import com.example.admin.friendconnection.object.Account;

import java.util.ArrayList;

/**
 * Created by Admin on 4/9/2018.
 */

public class LoginFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private View view;
    private EditText edtUser;
    private EditText edtPass;
    private SwitchCompat switchCompat;
    private Button btnLogin;
    private Button btnRes;
    private LoginActivity activity;
    private ArrayList<Account> accounts;
    private View vSnackbar;
    private Snackbar snackbar;
    private int tempIndex = 0;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String FILE = "FileData";
    public static final String USER = "user";
    public static final String PASS = "pass";
    public static final String LINK = "link";
    public static final String ID = "id";
    public static final String CHECK = "switch";
    public static final String NAME = "name";
    public static final String MAIL = "mail";
    public static final String SEX = "sex";
    public static final String PHONE = "phone";
    public static final String BIRTH = "birth";
    private boolean check;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (LoginActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_login, container, false);
        accounts = activity.getAccounts();
        sharedPreferences = activity.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        check = sharedPreferences.getBoolean(CHECK, true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        edtUser = activity.findViewById(R.id.edtUserLogin);
        edtPass = activity.findViewById(R.id.edtPassLogin);
        switchCompat = activity.findViewById(R.id.switchLogin);
        btnLogin = activity.findViewById(R.id.btnLogin);
        btnRes = activity.findViewById(R.id.btnNoAccount);
        snackbar = Snackbar.make(edtPass, "", Snackbar.LENGTH_SHORT);
        vSnackbar = snackbar.getView();
        btnLogin.setOnClickListener(this);
        btnRes.setOnClickListener(this);
        if (check) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }
        switchCompat.setOnCheckedChangeListener(this);
        if (switchCompat.isChecked()) {
            String user = sharedPreferences.getString(USER, "");
            String pass = sharedPreferences.getString(PASS, "");
            edtUser.setText(user);
            edtPass.setText(pass);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            if (activity.isNetworkConnected()) {
                String user = edtUser.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if (user.isEmpty()) {
                    showSnackbar(R.color.colorError, "Please enter your Username", R.drawable.ic_close_black_24dp);
                } else if (pass.isEmpty()) {
                    showSnackbar(R.color.colorError, "Please enter your Password", R.drawable.ic_close_black_24dp);
                } else {
                    if (checkAccount(user, pass)) {
                        showSnackbar(R.color.colorSuccess, "Login success", R.drawable.ic_check_black_24dp);
                        saveUserPass(user, pass);
                        intentActivity();
                    } else {
                        showSnackbar(R.color.colorError, "Incorrect account or password", R.drawable.ic_close_black_24dp);
                        edtPass.setText("");
                    }
                }
            } else {
                showSnackbar(R.color.colorError, "No network connection", R.drawable.ic_close_black_24dp);
            }
        } else if (v.getId() == R.id.btnNoAccount) {
            activity.showFragment(activity.getRegisterFramgent());

        }
    }

    public void setUserPass(String user, String pass) {
        edtUser.setText(user);
        edtPass.setText(pass);
    }

    private boolean checkAccount(String user, String pass) {
        for (int i = 0; i < accounts.size(); i++) {
            if (user.equals(accounts.get(i).getUserName()) && pass.equals(accounts.get(i).getPassWord())) {
                tempIndex = i;
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

    private void intentActivity() {

        // getActivity().bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        if (accounts.get(tempIndex).getName().isEmpty()) {
            Intent intent = new Intent(activity, InforUserActivity.class);
            intent.putExtra(InforUserActivity.KEYINTENT, accounts.get(tempIndex).getId());
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            activity.finish();
        } else {
            editor.putString(ID, accounts.get(tempIndex).getId());
            editor.putString(LINK, accounts.get(tempIndex).getLinkAvatar());
            editor.putString(NAME, accounts.get(tempIndex).getName());
            editor.putString(LoginFragment.MAIL, accounts.get(tempIndex).getMail());
            editor.putString(LoginFragment.PHONE, accounts.get(tempIndex).getPhone());
            editor.putString(LoginFragment.SEX, accounts.get(tempIndex).getSex());
            editor.putString(LoginFragment.BIRTH, accounts.get(tempIndex).getBirthDay());
            editor.commit();
            Intent intent = new Intent(activity, ActivityHome.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.anim_bounce, 0);
            activity.finish();
        }
    }

    private void saveUserPass(String user, String pass) {
        editor.putString(USER, user);
        editor.putString(PASS, pass);
        editor.putBoolean(CHECK, check);
        editor.commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        check = isChecked;
    }
}
