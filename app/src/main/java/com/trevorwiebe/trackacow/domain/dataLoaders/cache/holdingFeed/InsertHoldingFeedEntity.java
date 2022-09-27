package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingFeed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingFeedEntity;

public class InsertHoldingFeedEntity extends AsyncTask<Context, Void, Void> {

    private HoldingFeedEntity holdingFeedEntity;

    public InsertHoldingFeedEntity(HoldingFeedEntity holdingFeedEntity){
        this.holdingFeedEntity = holdingFeedEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingFeedDao().insertHoldingFeed(holdingFeedEntity);
        return null;
    }
}
