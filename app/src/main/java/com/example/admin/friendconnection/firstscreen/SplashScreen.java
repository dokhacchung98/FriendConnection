package com.example.admin.friendconnection.firstscreen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginActivity;
import com.example.admin.friendconnection.login.LoginFragment;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {
    private CountDownTimer countDownTimer;
    private ImageView img1, img2;
    private FrameLayout frameLayout;
    private Animation animation1, animation2;
    public String[] per = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };
    public static final String STARTFIRST = "StartFirst";
    private SharedPreferences sharedPreferences;
    private boolean check = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        img1 = findViewById(R.id.imgLogo);
        img2 = findViewById(R.id.imgText);
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setOnClickListener(this);
        frameLayout.setEnabled(false);

        countDownTimer = new CountDownTimer(3000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                intentHome();
            }
        };
        animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_logo);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_text);

        img1.setAnimation(animation1);
        img2.setAnimation(animation2);
        animation1.start();
        animation2.start();
        animation1.setAnimationListener(this);
    }

    @Override
    public void onClick(View v) {
        countDownTimer.cancel();
        intentHome();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent(SplashScreen.this, PermissionActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        intentHome();
    }

    private void intentHome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permisson : per) {
                if (checkSelfPermission(permisson) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(per, 0);
                    return;
                }
            }
        }
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        check = sharedPreferences.getBoolean(STARTFIRST, false);
        Intent intent;
        if (!check) {
            intent = new Intent(SplashScreen.this, SlideActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        frameLayout.setEnabled(true);
        countDownTimer.start();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
