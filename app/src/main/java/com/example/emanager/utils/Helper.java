package com.example.emanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Helper {
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static boolean isSameDate(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}

