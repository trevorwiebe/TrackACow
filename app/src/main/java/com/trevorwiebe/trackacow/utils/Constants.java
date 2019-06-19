package com.trevorwiebe.trackacow.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static final DatabaseReference BASE_REFERENCE = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    public static final int SUCCESS = 1;
    public static final int NO_NETWORK_CONNECTION = 2;
    public static final int ERROR_FETCHING_DATA_FROM_CLOUD = 3;
    public static final int ERROR_PUSHING_DATA_TO_CLOUD = 4;

    public static final int MEDICATE = 1;
    public static final int FEED = 2;
    public static final int MOVE = 3;
    public static final int REPORTS = 4;
    public static final int MORE = 5;

    /* holding entity 'what happened' keys */
    public static final int INSERT_UPDATE = 1;
    public static final int DELETE = 3;

    public static final int LOT = 1;
    public static final int ARCHIVE = 2;
}
