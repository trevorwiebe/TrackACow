package com.trevorwiebe.trackacow.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String convertMillisToDate(long longDate){
        SimpleDateFormat format = new SimpleDateFormat("M/d/y", Locale.getDefault());
        Date date = new Date(longDate);
        return format.format(date);
    }

    public static void showNotification(Context context, String channelId, String title, String contentText) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification_icon_24dp)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(2, notificationBuilder.build());

    }

    public static void setUpNotificationChannels(Context context, String channelId, String channelName, String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void saveLastUsedScreen(Context context, int lastUsedScreen) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_used_screen_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.last_used_screen_key), lastUsedScreen);
        editor.apply();
    }

    public static int getLastUsedScreen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_used_screen_name), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getResources().getString(R.string.last_used_screen_key), Constants.MEDICATE);
    }

    public static void setNewDataToUpload(Context context, boolean isThereNewData) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getApplicationContext().getResources().getString(R.string.new_data_to_upload_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getApplicationContext().getResources().getString(R.string.new_data_to_upload_key), isThereNewData);
        editor.apply();
    }

    public static boolean isThereNewDataToUpload(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getApplicationContext().getResources().getString(R.string.new_data_to_upload_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getApplicationContext().getResources().getString(R.string.new_data_to_upload_key), false);
    }

    public static void setPenId(Context context, String penId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getApplicationContext().getResources().getString(R.string.save_pen_id_name),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getApplicationContext().getResources().getString(R.string.save_pen_id_key), penId);
        editor.apply();
    }

    public static String getPenId(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getApplicationContext().getResources().getString(R.string.save_pen_id_name),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(activity.getApplicationContext().getResources().getString(R.string.save_pen_id_key), "");
    }

    public static void setCowId(Activity activity, String cowId) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getApplicationContext().getResources().getString(R.string.save_cow_id_name),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(activity.getApplicationContext().getResources().getString(R.string.save_cow_id_key), cowId);
        editor.apply();
    }

    public static String getCowId(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getApplicationContext().getResources().getString(R.string.save_cow_id_name),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(activity.getApplicationContext().getResources().getString(R.string.save_cow_id_key), "");
    }

    public static ArrayList<DrugsGivenEntity> findDrugsGivenEntityByCowId(String cowId, ArrayList<DrugsGivenEntity> drugsGivenEntities){
        ArrayList<DrugsGivenEntity> drugsGivenToCow = new ArrayList<>();
        for(int r=0; r<drugsGivenEntities.size(); r++){
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(r);
            if(drugsGivenEntity.getCowId().equals(cowId)){
                drugsGivenToCow.add(drugsGivenEntity);
            }
        }
        return drugsGivenToCow;
    }

    public static DrugEntity findDrugEntity(String drugId, ArrayList<DrugEntity> drugEntities){
        for(int r=0; r<drugEntities.size(); r++){
            DrugEntity drugEntity = drugEntities.get(r);
            if(drugEntity.getDrugId().equals(drugId)){
                return drugEntity;
            }
        }
        return null;
    }

}
