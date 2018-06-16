package com.example.admin.friendconnection.firstscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.admin.friendconnection.R;

public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnOk;
    public String[] per = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        btnOk = findViewById(R.id.btnOkk);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        requesPer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        intentHome();
    }

    private void intentHome() {
        Intent intent = new Intent(PermissionActivity.this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    private void requesPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permisson : per) {
                if (checkSelfPermission(permisson) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(per, 0);
                    return;
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
