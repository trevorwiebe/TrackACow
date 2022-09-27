package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingPen;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

public class InsertHoldingPen extends AsyncTask<Context, Void, Void> {

    private CachePenEntity cachePenEntity;

    public InsertHoldingPen(CachePenEntity cachePenEntity){
        this.cachePenEntity = cachePenEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cachePenDao().insertHoldingPen(cachePenEntity);
        return null;
    }

}
