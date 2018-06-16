package com.example.admin.friendconnection.firstscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginActivity;
import com.example.admin.friendconnection.login.LoginFragment;

public class SlideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    private TextView txtDot[];
    private LinearLayout linearLayout;
    private Button btnBack;
    private Button btnNext;
    private Button btnFinsh;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        viewPager = findViewById(R.id.vpSlide);
        viewPager.setOnPageChangeListener(this);
        linearLayout = findViewById(R.id.lnDot);
        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnFinsh = findViewById(R.id.btnFinish);
        btnFinsh.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        slideAdapter = new SlideAdapter(this);
        viewPager.setAdapter(slideAdapter);
        addDot(0);
    }

    @SuppressLint("ResourceAsColor")
    private void addDot(int pos) {
        txtDot = new TextView[3];
        linearLayout.removeAllViews();
        for (int i = 0; i < txtDot.length; i++) {
            txtDot[i] = new TextView(this);
            txtDot[i].setText(Html.fromHtml("&#8226;"));
            txtDot[i].setTextSize(35);
            if (i != pos) {
                txtDot[i].setTextColor(getResources().getColor(R.color.colorTextHint));
            } else {
                txtDot[i].setTextColor(getResources().getColor(R.color.colorWhite));
            }
            linearLayout.addView(txtDot[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        page = position;
        addDot(position);
        if (position == 0) {
            btnBack.setVisibility(View.GONE);
            btnFinsh.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        } else if (position == (txtDot.length - 1)) {
            btnBack.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnFinsh.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnFinsh.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                if (page > 0) {
                    viewPager.setCurrentItem(page - 1);
                }
                break;
            case R.id.btnNext:
                if (page < txtDot.length - 1) {
                    viewPager.setCurrentItem(page + 1);
                }
                break;
            case R.id.btnFinish:
                editor.putBoolean(SplashScreen.STARTFIRST, true);
                editor.commit();
                Intent intent = new Intent(SlideActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                this.finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
