package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.local.entities.LotEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryLots extends AsyncTask<Context, Void, ArrayList<LotEntity>> {

    public OnLotsLoaded onLotsLoaded;

    public QueryLots(OnLotsLoaded onLotsLoaded) {
        this.onLotsLoaded = onLotsLoaded;
    }

    public interface OnLotsLoaded {
        void onLotsLoaded(ArrayList<LotEntity> lotEntities);
    }

    @Override
    protected ArrayList<LotEntity> doInBackground(Context... contexts) {
        List<LotEntity> lotEntities = AppDatabase.getAppDatabase(contexts[0]).lotDao().getLotEntityList();
        return (ArrayList<LotEntity>) lotEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<LotEntity> lotEntities) {
        super.onPostExecute(lotEntities);
        onLotsLoaded.onLotsLoaded(lotEntities);
    }
}
