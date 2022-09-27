package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingFeed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

public class InsertHoldingFeedEntity extends AsyncTask<Context, Void, Void> {

    private CacheFeedEntity cacheFeedEntity;

    public InsertHoldingFeedEntity(CacheFeedEntity cacheFeedEntity){
        this.cacheFeedEntity = cacheFeedEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheFeedDao().insertHoldingFeed(cacheFeedEntity);
        return null;
    }
}
