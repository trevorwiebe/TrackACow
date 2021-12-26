package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingFeedEntity;

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
