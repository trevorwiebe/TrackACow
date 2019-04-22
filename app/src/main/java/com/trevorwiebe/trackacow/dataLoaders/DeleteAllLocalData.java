package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteAllLocalData extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        db.cowDao().deleteCowTable();
        db.drugDao().deleteDrugTable();
        db.drugsGivenDao().deleteDrugsGivenTable();
        db.penDao().deletePenTable();
        db.lotDao().deleteLotEntityTable();
        db.archivedLotDao().deleteArchivedLotTable();
        db.callDao().deleteCallTable();
        db.feedDao().deleteFeedTable();
        db.userDao().deleteUserTable();
        db.loadDao().deleteLoadTable();

        db.holdingCowDao().deleteHoldingCowTable();
        db.holdingDrugsGivenDao().deleteHoldingDrugsGivenTable();
        db.holdingDrugDao().deleteHoldingDrugTable();
        db.holdingPenDao().deleteHoldingPenTable();
        db.holdingLotDao().deleteHoldingLotTable();
        db.holdingArchivedLotDao().deleteHoldingArchivedLotTable();
        db.holdingLoadDao().deleteHoldingLoadTable();

        return null;
    }
}
