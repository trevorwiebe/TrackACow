package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.LotEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryLotsByPenId extends AsyncTask<Context, Void, ArrayList<LotEntity>> {

    private String penId;
    private OnLotsByPenIdLoaded onLotsByPenIdLoaded;

    public QueryLotsByPenId(String penId, OnLotsByPenIdLoaded onLotsByPenIdLoaded) {
        this.penId = penId;
        this.onLotsByPenIdLoaded = onLotsByPenIdLoaded;
    }

    public interface OnLotsByPenIdLoaded {
        void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities);
    }

    @Override
    protected ArrayList<LotEntity> doInBackground(Context... contexts) {
        List<LotEntity> lotEntities = AppDatabase.getAppDatabase(contexts[0]).lotDao().getLotEntitiesByPenId2(penId);
        return (ArrayList<LotEntity>) lotEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<LotEntity> lotEntities) {
        super.onPostExecute(lotEntities);
        onLotsByPenIdLoaded.onLotsByPenIdLoaded(lotEntities);
    }
}
