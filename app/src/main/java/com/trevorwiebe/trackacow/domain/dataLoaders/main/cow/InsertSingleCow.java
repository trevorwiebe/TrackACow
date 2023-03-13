package com.trevorwiebe.trackacow.domain.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.CowEntity;

@Deprecated(since="Use use-cases instead")
public class InsertSingleCow extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "InsertSingleCow";

    private CowEntity mCowEntity;

    public InsertSingleCow(CowEntity cowEntity){
        this.mCowEntity = cowEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().insertCow2(mCowEntity);
        return null;
    }
}
