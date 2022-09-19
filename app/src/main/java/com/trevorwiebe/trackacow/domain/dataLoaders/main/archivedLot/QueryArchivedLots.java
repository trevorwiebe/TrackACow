package com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.ArchivedLotEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryArchivedLots extends AsyncTask<Context, Void, ArrayList<ArchivedLotEntity>> {

    private OnArchivedLotsLoaded onArchivedLotsLoaded;

    public QueryArchivedLots(OnArchivedLotsLoaded onArchivedLotsLoaded) {
        this.onArchivedLotsLoaded = onArchivedLotsLoaded;
    }

    public interface OnArchivedLotsLoaded {
        void onArchivedLotsLoaded(ArrayList<ArchivedLotEntity> archivedLotEntities);
    }

    @Override
    protected ArrayList<ArchivedLotEntity> doInBackground(Context... contexts) {
        List<ArchivedLotEntity> archivedLotEntityList = AppDatabase.getAppDatabase(contexts[0]).archivedLotDao().getArchiveLots();
        return (ArrayList<ArchivedLotEntity>) archivedLotEntityList;
    }

    @Override
    protected void onPostExecute(ArrayList<ArchivedLotEntity> archivedLotEntities) {
        super.onPostExecute(archivedLotEntities);
        onArchivedLotsLoaded.onArchivedLotsLoaded(archivedLotEntities);
    }
}
