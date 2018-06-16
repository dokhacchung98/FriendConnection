package com.example.admin.friendconnection.friend;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.admin.friendconnection.R;

public class FriendActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private AdapterViewPagerFriend adapterViewPagerFriend;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        viewPager = findViewById(R.id.vpFriend);
        tabLayout = findViewById(R.id.tabFriend);
        adapterViewPagerFriend = new AdapterViewPagerFriend(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPagerFriend);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_frend_tablayout);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_add_frend_tablayout);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
