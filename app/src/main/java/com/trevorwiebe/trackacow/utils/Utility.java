package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String convertMillisToDate(long longDate){
        SimpleDateFormat format = new SimpleDateFormat("M/d/y", Locale.getDefault());
        Date date = new Date(longDate);
        return format.format(date);
    }

    public static Long convertDateToMillis(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("M/d/y", Locale.getDefault());
        try {
            Date date_value = formatter.parse(date);
            return date_value.getTime();
        }catch(Exception e){
            return null;
        }
    }

    public static void vibrate(Context context, long millisToVibrate){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(millisToVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(millisToVibrate);
            }
        }
    }

}
