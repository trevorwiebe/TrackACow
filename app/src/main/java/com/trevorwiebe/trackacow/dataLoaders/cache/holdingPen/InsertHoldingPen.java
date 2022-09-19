package com.trevorwiebe.trackacow.dataLoaders.cache.holdingPen;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;

public class InsertHoldingPen extends AsyncTask<Context, Void, Void> {

    private HoldingPenEntity holdingPenEntity;

    public InsertHoldingPen(HoldingPenEntity holdingPenEntity){
        this.holdingPenEntity = holdingPenEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingPenDao().insertHoldingPen(holdingPenEntity);
        return null;
    }

}
