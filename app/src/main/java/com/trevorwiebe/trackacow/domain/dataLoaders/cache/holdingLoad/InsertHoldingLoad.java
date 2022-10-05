package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLoad;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity;

@Deprecated(since="Use use-cases instead")
public class InsertHoldingLoad extends AsyncTask<Context, Void, Void> {

    private CacheLoadEntity cacheLoadEntity;

    public InsertHoldingLoad(CacheLoadEntity cacheLoadEntity) {
        this.cacheLoadEntity = cacheLoadEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheLoadDao().insertHoldingLoad(cacheLoadEntity);
        return null;
    }
}
