package com.trevorwiebe.trackacow.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String convertMillisToDate(long longDate){

        SimpleDateFormat format = new SimpleDateFormat("M/d/y", Locale.getDefault());
        Date date = new Date(longDate);
        return format.format(date);

    }
}
