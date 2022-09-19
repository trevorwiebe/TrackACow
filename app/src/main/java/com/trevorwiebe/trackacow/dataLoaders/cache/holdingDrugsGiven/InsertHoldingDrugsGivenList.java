package com.trevorwiebe.trackacow.dataLoaders.cache.holdingDrugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

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
