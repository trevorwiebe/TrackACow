package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;

public class SetMedicatedCow extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "SetMedicatedCow";

    private CowEntity mCowEntity;

    public SetMedicatedCow(CowEntity cowEntity){
        this.mCowEntity = cowEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().insertCow(mCowEntity);
        return null;
    }
}
