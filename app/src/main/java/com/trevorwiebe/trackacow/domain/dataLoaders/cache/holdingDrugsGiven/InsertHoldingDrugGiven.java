package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingDrugsGivenEntity;

public class InsertHoldingDrugGiven extends AsyncTask<Context, Void, Void> {

    private HoldingDrugsGivenEntity holdingDrugsGivenEntity;

    public InsertHoldingDrugGiven(HoldingDrugsGivenEntity holdingDrugsGivenEntity){
        this.holdingDrugsGivenEntity = holdingDrugsGivenEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingDrugsGivenDao().insertHoldingDrugsGiven(holdingDrugsGivenEntity);
        return null;
    }
}
