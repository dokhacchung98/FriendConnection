package com.example.admin.friendconnection.model;

import android.util.Log;

/**
 * Created by Admin on 4/12/2018.
 */

public class ConvertPass {
    private StringBuilder stringBuilder;
    private static final int KEYCODE = 35;

    public String convert(String password) {
        stringBuilder = new StringBuilder(password);
        convert1(0, password.length() - 1);
        return stringBuilder.toString();
    }

    private void convert1(int left, int right) {
        if (left < right) {
            int n = (left + right) / 2;
            convert1(left, n);
            convert1(n + 1, right);
        }
        for (int i = left; i <= right; i++) {
            stringBuilder.setCharAt(i, (char) (stringBuilder.codePointAt(i) + KEYCODE));
        }
    }

    public String deConvert(String password) {
        stringBuilder = new StringBuilder(password);
        deConvert1(0, password.length() - 1);
        return stringBuilder.toString();
    }

    private void deConvert1(int left, int right) {
        if (left < right) {
            int n = (left + right) / 2;
            deConvert1(left, n);
            deConvert1(n + 1, right);
        }
        for (int i = left; i <= right; i++) {
            stringBuilder.setCharAt(i, (char) (stringBuilder.codePointAt(i) - KEYCODE));
        }
    }
}
