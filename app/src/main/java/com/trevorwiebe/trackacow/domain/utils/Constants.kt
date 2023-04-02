package com.trevorwiebe.trackacow.domain.utils

import com.google.firebase.auth.FirebaseAuth

object Constants {

    val BASE_REFERENCE_STRING = "/users/" + FirebaseAuth.getInstance().currentUser!!.uid + "/"
    const val DATABASE_STRING_ARCHIVED_LOT = "/archives"
    const val ARCHIVE_LOT = "archives"
    const val DATABASE_STRING_RATIONS = "rations"
    const val RATIONS = "rations"
    const val DATABASE_STRING_CALLS = "calls"
    const val CALLS = "calls"
    const val DATABASE_STRING_PENS = "/pens"
    const val PENS = "pens"
    const val DATABASE_STRING_LOT = "/cattleLot"
    const val LOTS = "cattleLot"
    const val DATABASE_STRING_DRUGS = "drugs/"
    const val DRUG = "drugs"
    const val DATABASE_STRING_FEEDS = "/feed"
    const val FEEDS = "feed"
    const val DATABASE_STRING_LOAD = "/loads"
    const val LOAD = "loads"
    const val DATABASE_STRING_DRUGS_GIVEN = "drugsGiven"
    const val DRUGS_GIVEN = "drugsGiven"
    const val DATABASE_STRING_COW = "cows"
    const val COW = "cows"
    const val DATABASE_STRING_USER = "/user"
    const val USER = "user"
    const val FREE_TRIAL = 0
    const val MONTHLY_SUBSCRIPTION = 1
    const val ANNUAL_SUBSCRIPTION = 2
    const val CANCELED = 6
    const val FOREVER_FREE_USER = 7
    const val SUCCESS = 1
    const val NO_NETWORK_CONNECTION = 2
    const val ERROR_FETCHING_DATA_FROM_CLOUD = 3
    const val ERROR_PUSHING_DATA_TO_CLOUD = 4
    const val ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED = 5
    const val MEDICATE = 1
    const val FEED = 2
    const val MOVE = 3
    const val REPORTS = 4
    const val MORE = 5

    /* holding entity 'what happened' keys */
    const val INSERT_UPDATE = 1
    const val DELETE = 3
    const val LOT = 1
    const val ARCHIVE = 2

    /* time drug report types */
    const val DAY_DRUG_REPORT = 1
    const val WEEK_DRUG_REPORT = 2
    const val MONTH_DRUG_REPORT = 3

    // Preference keys
    const val NEW_DATA_TO_UPLOAD_NAME = "new_data_to_upload_name"
    const val NEW_DATA_TO_UPLOAD_KEY = "new_data_to_upload_key"

    // Drug report types
    const val YESTERDAY = 1
    const val MONTH = 2
    const val ALL = 3
    const val CUSTOM = 4

    // Manage Ration Type
    const val ADD_RATION = 1
    const val EDIT_RATION = 2
}