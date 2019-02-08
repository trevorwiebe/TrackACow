package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteLocalHoldingData extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);
        db.holdingDrugDao().deleteHoldingDrugTable();
        db.holdingDrugsGivenDao().deleteHoldingDrugsGivenTable();
        db.holdingPenDao().deleteHoldingPenTable();
        db.holdingCowDao().deleteHoldingCowTable();
        return null;
    }

}
