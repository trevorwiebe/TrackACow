package com.trevorwiebe.trackacow.data.preferences

import android.content.SharedPreferences
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences

class AppPreferencesImpl(
    private val sharedPref: SharedPreferences
): AppPreferences {

    override fun saveLastUsedRation(rationId: Int) {
        sharedPref.edit()
            .putInt(AppPreferences.KEY_LAST_USED_RATION, rationId)
            .apply()
    }

    override fun getLastUsedRation(rationKey: String): Int {
        return sharedPref.getInt(rationKey, -1)
    }

}