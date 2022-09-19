package com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.ArchivedLotEntity;

public class InsertArchivedLotEntity extends AsyncTask<Context, Void, Void> {

    private ArchivedLotEntity archivedLotEntity;

    public InsertArchivedLotEntity(ArchivedLotEntity archivedLotEntity) {
        this.archivedLotEntity = archivedLotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).archivedLotDao().insertArchivedLotEntity(archivedLotEntity);
        return null;
    }


}
