package com.trevorwiebe.trackacow.domain.dataLoaders.misc;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class DeleteAllLocalData extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        assert db != null;
        db.cowDao().deleteCowTable();
        db.drugDao().deleteDrugTable();
        db.drugsGivenDao().deleteDrugsGivenTable();
        db.penDao().deletePenTable();
        db.lotDao().deleteLotEntityTable();
        db.archivedLotDao().deleteArchivedLotTable();
//        db.callDao().deleteCallTable();
        db.feedDao().deleteFeedTable();
        db.userDao().deleteUserTable();
        db.loadDao().deleteLoadTable();

        db.cacheCowDao().deleteHoldingCowTable();
        db.cacheDrugsGivenDao().deleteHoldingDrugsGivenTable();
        db.cacheDrugDao().deleteHoldingDrugTable();
        db.cachePenDao().deleteHoldingPenTable();
        db.cacheLotDao().deleteHoldingLotTable();
        db.cacheArchivedLotDao().deleteHoldingArchivedLotTable();
        db.cacheCallDao().deleteCallTable();
        db.cacheFeedDao().deleteHoldingFeedTable();
        db.cacheUserDao().deleteHoldingUserTable();
        db.cacheLoadDao().deleteHoldingLoadTable();

        return null;
    }
}
