package com.trevorwiebe.trackacow.domain.dataLoaders.main.load;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.local.entities.LoadEntity;

public class QueryLoadsByLoadId extends AsyncTask<Context, Void, LoadEntity> {

    private String loadId;
    private OnLoadsByLoadIdLoaded onLoadsByLoadIdLoaded;

    public QueryLoadsByLoadId(String loadId, OnLoadsByLoadIdLoaded onLoadsByLoadIdLoaded) {
        this.loadId = loadId;
        this.onLoadsByLoadIdLoaded = onLoadsByLoadIdLoaded;
    }

    public interface OnLoadsByLoadIdLoaded {
        void onLoadsByLoadIdLoaded(LoadEntity loadEntity);
    }

    @Override
    protected LoadEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).loadDao().getLoadByLoadId(loadId);
    }

    @Override
    protected void onPostExecute(LoadEntity loadEntity) {
        super.onPostExecute(loadEntity);
        onLoadsByLoadIdLoaded.onLoadsByLoadIdLoaded(loadEntity);
    }
}
