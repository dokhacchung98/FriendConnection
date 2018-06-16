package com.example.admin.friendconnection.friend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Admin on 4/21/2018.
 */

public class AdapterViewPagerFriend extends FragmentStatePagerAdapter {

    public AdapterViewPagerFriend(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new FriendFragment1();
            }
            case 1: {
                return new AddFriendFragment();
            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
//        switch (position) {
//            case 0:
//                return "Firend";
//            case 1:
//                return "Add Friend";
//            default:
//                return null;
//        }
    }
}
