package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCall;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity;

public class InsertHoldingCall extends AsyncTask<Context, Void, Void> {

    private CacheCallEntity cacheCallEntity;

    public InsertHoldingCall(CacheCallEntity cacheCallEntity){
        this.cacheCallEntity = cacheCallEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheCallDao().insertHoldingCall(cacheCallEntity);
        return null;
    }
}
