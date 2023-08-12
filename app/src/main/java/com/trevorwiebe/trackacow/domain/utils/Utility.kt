package com.trevorwiebe.trackacow.domain.utils

import android.content.Context
import android.net.ConnectivityManager
import com.trevorwiebe.trackacow.R
import java.text.SimpleDateFormat
import java.util.*


object Utility {

    fun convertMillisToDate(longDate: Long): String {
        val format = SimpleDateFormat("M/d/y", Locale.getDefault())
        val date = Date(longDate)
        return format.format(date)
    }


    @JvmStatic
    fun haveNetworkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null &&
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
    }

    fun saveLastUsedScreen(context: Context, lastUsedScreen: Int) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_used_screen_name),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(context.resources.getString(R.string.last_used_screen_key), lastUsedScreen)
        editor.apply()
    }

    fun getLastUsedScreen(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_used_screen_name),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(
            context.resources.getString(R.string.last_used_screen_key),
            Constants.MEDICATE
        )
    }

    fun saveLastUsedRation(context: Context, rationId: Int) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.save_ration_id_name),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(context.resources.getString(R.string.save_ration_id_key), rationId)
        editor.apply()
    }

    fun getLastUsedRation(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.save_ration_id_name),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(
            context.resources.getString(R.string.save_ration_id_key),
            -1
        )
    }

    fun saveLastFeedPen(context: Context, lastFeedPen: Int) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_used_feed_pen_name),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(context.resources.getString(R.string.last_used_feed_pen_key), lastFeedPen)
        editor.apply()
    }

    fun getLastFeedPen(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_used_feed_pen_name),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(
            context.resources.getString(R.string.last_used_feed_pen_key),
            2
        )
    }

    @JvmStatic
    fun setNewDataToUpload(context: Context, isThereNewData: Boolean) {
        val sharedPreferences =
            context.getSharedPreferences(
                context.getString(R.string.new_data_to_upload_name),
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.new_data_to_upload_key), isThereNewData)
        editor.apply()
    }

    @JvmStatic
    fun isThereNewDataToUpload(context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(
                context.getString(R.string.new_data_to_upload_name),
                Context.MODE_PRIVATE
            )
        return sharedPreferences.getBoolean(
            context.getString(R.string.new_data_to_upload_key),
            false
        )
    }

    @JvmStatic
    fun setLastSync(context: Context, lastSync: Long) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_sync_name),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putLong(context.resources.getString(R.string.last_sync_key), lastSync)
        editor.apply()
    }

    fun getLastSync(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.last_sync_name),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getLong(context.resources.getString(R.string.last_sync_key), 0)
    }

    fun setShouldShowTrialEndsSoon(context: Context, shouldShow: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.should_show_trial_ends_name),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(
            context.resources.getString(R.string.should_show_trial_ends_key),
            shouldShow
        )
        editor.apply()
    }

    fun shouldShowTrialEndsSoon(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.should_show_trial_ends_name),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.should_show_trial_ends_key),
            true
        )
    }

    fun convertMillisToFriendlyDate(date: Long): String {
        var today = Calendar.getInstance()
        today = clearTimes(today)
        var yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        yesterday = clearTimes(yesterday)
        var tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        tomorrow = clearTimes(tomorrow)
        return if (date >= tomorrow.timeInMillis && tomorrow.timeInMillis >= date) {
            "Tomorrow"
        } else if (date >= today.timeInMillis && today.timeInMillis >= date) {
            "Today"
        } else if (date >= yesterday.timeInMillis && yesterday.timeInMillis >= date) {
            "Yesterday"
        } else {
            convertMillisToDate(date)
        }
    }

    fun clearTimes(c: Calendar): Calendar {
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        return c
    }

    fun getFragmentIdFromResourceID(id: Int): Int {
        return when (id) {
            R.id.action_medicate -> Constants.MEDICATE
            R.id.action_feed -> Constants.FEED
            R.id.action_reports -> Constants.REPORTS
            R.id.action_more -> Constants.MORE
            else -> Constants.MOVE
        }
    }

    fun getResourceIdFromFragmentId(id: Int): Int {
        return when (id) {
            R.id.action_medicate -> Constants.MEDICATE
            R.id.action_feed -> Constants.FEED
            R.id.action_reports -> Constants.REPORTS
            R.id.action_more -> Constants.MORE
            else -> Constants.MOVE
        }
    }
}