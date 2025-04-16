package com.example.emanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {
    // Helper.java
    public static String formatDate(Date date) {
        if (date == null) return "No date"; // or return ""; or return a default date string
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
