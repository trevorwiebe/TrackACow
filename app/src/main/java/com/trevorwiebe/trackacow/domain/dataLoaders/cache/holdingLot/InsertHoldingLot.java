package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity;

public class InsertHoldingLot extends AsyncTask<Context, Void, Void> {

    private CacheLotEntity cacheLotEntity;


    public InsertHoldingLot(CacheLotEntity cacheLotEntity) {
        this.cacheLotEntity = cacheLotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheLotDao().insertHoldingLot(cacheLotEntity);
        return null;
    }
}
