package com.example.admin.friendconnection.model;

public class ConvertName {
    private String x(String str) {
        str = str.trim();
        str = str.replaceAll("\\s+", " ");
        return str;
    }

    public String formatName(String str) {
        str = x(str);
        String temp[] = str.split(" ");
        str = "";
        for (int i = 0; i < temp.length; i++) {
            str += String.valueOf(temp[i].charAt(0)).toUpperCase() + temp[i].substring(1);
            if (i < temp.length - 1)
                str += " ";
        }
        return str;
    }
}
