package com.trevorwiebe.trackacow.dataLoaders.cache.holdingLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;

public class InsertHoldingLot extends AsyncTask<Context, Void, Void> {

    private HoldingLotEntity holdingLotEntity;


    public InsertHoldingLot(HoldingLotEntity holdingLotEntity) {
        this.holdingLotEntity = holdingLotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingLotDao().insertHoldingLot(holdingLotEntity);
        return null;
    }
}
