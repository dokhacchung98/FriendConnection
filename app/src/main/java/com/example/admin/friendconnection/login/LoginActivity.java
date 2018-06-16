package com.example.admin.friendconnection.login;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.action.InforUserActivity;
import com.example.admin.friendconnection.home.ActivityHome;
import com.example.admin.friendconnection.model.ConvertPass;
import com.example.admin.friendconnection.object.Account;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private LoginFragment loginFragment = new LoginFragment();
    private RegisterFramgent registerFramgent = new RegisterFramgent();
    private long timePressBack;
    private ArrayList<Account> accounts;
    private DatabaseReference databaseReference;
    private ConvertPass convertPass;
    private SpinKitView spinKitView;
    private SharedPreferences sharedPreferences;
    private boolean loadFinish = false;
    private String user;
    private String pas;

    public LoginFragment getLoginFragment() {
        return loginFragment;
    }

    public RegisterFramgent getRegisterFramgent() {
        return registerFramgent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accounts = new ArrayList<>();
        convertPass = new ConvertPass();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        user = sharedPreferences.getString(LoginFragment.USER, "");
        pas = sharedPreferences.getString(LoginFragment.PASS, "");
        spinKitView = findViewById(R.id.spin_kit);
//        Style style = Style.values()[9];
//        Sprite drawable = SpriteFactory.create(style);
//        spinKitView.setIndeterminateDrawable(drawable);
        getAccoutInFirebase();
        initFragment();

    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLR, loginFragment);
        fragmentTransaction.add(R.id.frameLR, registerFramgent);
        fragmentTransaction.hide(registerFramgent);
        fragmentTransaction.show(loginFragment);
        fragmentTransaction.commit();
        //showFragment(loginFragment);
    }

    public void showFragment(Fragment fragment) {
        hideFragment(fragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment.equals(getLoginFragment())) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_l, R.anim.slide_out_r);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment.equals(getLoginFragment())) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_l, R.anim.slide_out_r);
        }
        fragmentTransaction.hide(loginFragment);
        fragmentTransaction.hide(registerFramgent);
        fragmentTransaction.commit();
    }

    public void setUserPass(String user, String pass) {
        loginFragment.setUserPass(user, pass);
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

    private void getAccoutInFirebase() {
        accounts.clear();
        databaseReference.child("Account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account account = dataSnapshot.getValue(Account.class);
                account.setPassWord(convertPass.deConvert(account.getPassWord()));
                if (account.getUserName().equals(user) && account.getPassWord().equals(pas) && isServiceRunning() && !account.getName().isEmpty()) {
                    autoLogin();
                } else if (account.getUserName().equals(user) && account.getPassWord().equals(pas) && isServiceRunning() && account.getName().isEmpty()) {
                    autologinInfor(account.getId());
                }
                accounts.add(account);
                if (accounts.size() > 0) {
                    loadFinish = true;
                }

                if (!loadFinish) {
                    spinKitView.setVisibility(View.VISIBLE);
                } else {
                    spinKitView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAccoutInFirebase();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getAccoutInFirebase();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getAccoutInFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getAccoutInFirebase();
            }
        });
    }

    private void autologinInfor(String idd) {
        Intent intent = new Intent(this, InforUserActivity.class);
        intent.putExtra(InforUserActivity.KEYINTENT, idd);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void autoLogin() {
        Intent intent = new Intent(this, ActivityHome.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_bounce, 0);
        this.finish();
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        }
        return true;
    }


    public boolean isForeground() {
        String PackageName = "com.example.admin.friendconnection.MyService";
        // Get the Activity Manager
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

        // Get the info we need for comparison.
        ComponentName componentInfo = task.get(0).topActivity;

        // Check if it matches our package name.
        if (componentInfo.getPackageName().equals(PackageName)) return true;

        // If not then our app is not on the foreground.
        return false;
    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.admin.friendconnection.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void isLoading() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    public void isLoadingFinish() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
