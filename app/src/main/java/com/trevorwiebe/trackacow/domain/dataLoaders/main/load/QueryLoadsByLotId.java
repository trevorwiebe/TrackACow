package com.trevorwiebe.trackacow.domain.dataLoaders.main.load;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryLoadsByLotId extends AsyncTask<Context, Void, ArrayList<LoadEntity>> {

    private String lotId;
    private OnLoadsByLotIdLoaded onLoadsByLotIdLoaded;

    public QueryLoadsByLotId(String lotId, OnLoadsByLotIdLoaded onLoadsByLotIdLoaded) {
        this.lotId = lotId;
        this.onLoadsByLotIdLoaded = onLoadsByLotIdLoaded;
    }

    public interface OnLoadsByLotIdLoaded {
        void onLoadsByLotIdLoaded(ArrayList<LoadEntity> loadEntities);
    }

    @Override
    protected ArrayList<LoadEntity> doInBackground(Context... contexts) {
        List<LoadEntity> loadEntities = AppDatabase.getAppDatabase(contexts[0]).loadDao().getLoadsByLotId(lotId);
        return (ArrayList<LoadEntity>) loadEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<LoadEntity> loadEntities) {
        super.onPostExecute(loadEntities);
        onLoadsByLotIdLoaded.onLoadsByLotIdLoaded(loadEntities);
    }
}
