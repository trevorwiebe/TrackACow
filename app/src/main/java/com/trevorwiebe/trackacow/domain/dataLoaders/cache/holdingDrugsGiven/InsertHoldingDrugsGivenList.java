package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingDrugsGivenEntity;

import java.util.ArrayList;

public class InsertHoldingDrugsGivenList extends AsyncTask<Context, Void, Void> {

    private ArrayList<HoldingDrugsGivenEntity> holdingDrugsGivenEntities;

    public InsertHoldingDrugsGivenList(ArrayList<HoldingDrugsGivenEntity> holdingDrugsGivenEntities){
        this.holdingDrugsGivenEntities = holdingDrugsGivenEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingDrugsGivenDao().insertHoldingDrugsGivenList(holdingDrugsGivenEntities);
        return null;
    }
}
