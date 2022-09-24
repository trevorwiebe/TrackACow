package com.trevorwiebe.trackacow.domain.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.local.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.local.entities.DrugsGivenEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {

    private static final String TAG = "Utility";

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

    public static void saveLastFeedPen(Context context, int lastFeedPen) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_used_feed_pen_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.last_used_feed_pen_key), lastFeedPen);
        editor.apply();
    }

    public static int getLastFeedPen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_used_feed_pen_name), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getResources().getString(R.string.last_used_feed_pen_key), 2);
    }

    public static void setNewDataToUpload(Context context, boolean isThereNewData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NEW_DATA_TO_UPLOAD_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.NEW_DATA_TO_UPLOAD_KEY, isThereNewData);
        editor.apply();
    }

    public static boolean isThereNewDataToUpload(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NEW_DATA_TO_UPLOAD_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.NEW_DATA_TO_UPLOAD_KEY, false);
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

    public static void setLastSync(Context context, long lastSync) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_sync_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_sync_key), lastSync);
        editor.apply();
    }

    public static long getLastSync(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_sync_name), Context.MODE_PRIVATE);
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_sync_key), 0);
    }

    public static void setShouldShowTrialEndsSoon(Context context, boolean shouldShow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.should_show_trial_ends_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.should_show_trial_ends_key), shouldShow);
        editor.apply();
    }

    public static boolean shouldShowTrialEndsSoon(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.should_show_trial_ends_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.should_show_trial_ends_key), true);
    }

    public static void setShouldShowWelcomeScreen(Context context, boolean shouldShow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.should_show_welcome_screen_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.should_show_welcome_screen_key), shouldShow);
        editor.apply();
    }

    public static boolean shouldShowWelcomeScreen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.should_show_welcome_screen_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.should_show_welcome_screen_key), true);
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

    public static String convertMillisToFriendlyDate(long date) {
        Calendar today = Calendar.getInstance();
        today = clearTimes(today);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday = clearTimes(yesterday);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow = clearTimes(tomorrow);

        if (date >= tomorrow.getTimeInMillis() && tomorrow.getTimeInMillis() >= date) {
            return "Tomorrow";
        } else if (date >= today.getTimeInMillis() && today.getTimeInMillis() >= date) {
            return "Today";
        } else if (date >= yesterday.getTimeInMillis() && yesterday.getTimeInMillis() >= date) {
            return "Yesterday";
        } else {
            return convertMillisToDate(date);
        }
    }

    public static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static int getFragmentIdFromResourceID(int id){
        if(id == R.id.action_medicate) return Constants.MEDICATE;
        else if(id == R.id.action_feed) return Constants.FEED;
        else if(id == R.id.action_reports) return Constants.REPORTS;
        else if(id == R.id.action_more) return Constants.MORE;
        else return Constants.MOVE;
    }

    public static int getResourceIdFromFragmentId(int id){
        if(id == Constants.MEDICATE) return R.id.action_medicate;
        else if(id == Constants.FEED) return R.id.action_feed;
        else if(id == Constants.REPORTS) return R.id.action_reports;
        else if(id == Constants.MORE) return R.id.action_more;
        else return Constants.MOVE;
    }

}
