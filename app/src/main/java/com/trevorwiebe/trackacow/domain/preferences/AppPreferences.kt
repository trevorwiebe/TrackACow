package com.trevorwiebe.trackacow.domain.preferences

interface AppPreferences {

    fun saveLastUsedRation(rationId: Int)

    fun getLastUsedRation(rationKey: String): Int

    companion object {
        const val KEY_LAST_USED_RATION = "last_used_ration"
    }

}