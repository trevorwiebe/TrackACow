package com.trevorwiebe.trackacow.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;

public class InsertSingleCow extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "InsertSingleCow";

    private CowEntity mCowEntity;

    public InsertSingleCow(CowEntity cowEntity){
        this.mCowEntity = cowEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().insertCow(mCowEntity);
        return null;
    }
}
