package com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;

@Deprecated(since="Use use-cases instead")
public class QueryArchivedLotsByLotId extends AsyncTask<Context, Void, ArchivedLotEntity> {

    private String lotId;
    private OnArchivedLotLoaded onArchivedLotLoaded;

    public QueryArchivedLotsByLotId(String lotId, OnArchivedLotLoaded onArchivedLotLoaded) {
        this.lotId = lotId;
        this.onArchivedLotLoaded = onArchivedLotLoaded;
    }

    public interface OnArchivedLotLoaded {
        void onArchivedLotLoaded(ArchivedLotEntity archivedLotEntity);
    }

    @Override
    protected ArchivedLotEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).archivedLotDao().getArchivedLotById(lotId);
    }

    @Override
    protected void onPostExecute(ArchivedLotEntity archivedLotEntity) {
        super.onPostExecute(archivedLotEntity);
        onArchivedLotLoaded.onArchivedLotLoaded(archivedLotEntity);
    }
}
