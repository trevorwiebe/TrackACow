package com.trevorwiebe.trackacow.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String convertMillisToDate(long longDate){

        SimpleDateFormat format = new SimpleDateFormat("M/d/y, h:m a");
        Date date = new Date(longDate);
        return format.format(date);

    }
}
