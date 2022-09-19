package com.trevorwiebe.trackacow.domain.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.CowEntity;

public class QueryCowIdByCowId extends AsyncTask<Context, Void, CowEntity> {

    private String cowId;
    private OnCowByIdLoaded onCowByIdLoaded;

    public QueryCowIdByCowId(OnCowByIdLoaded onCowByIdLoaded, String cowId) {
        this.cowId = cowId;
        this.onCowByIdLoaded = onCowByIdLoaded;
    }

    public interface OnCowByIdLoaded {
        void onCowByIdLoaded(CowEntity cowEntity);
    }

    @Override
    protected CowEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).cowDao().getCowById(cowId);
    }

    @Override
    protected void onPostExecute(CowEntity cowEntity) {
        super.onPostExecute(cowEntity);
        onCowByIdLoaded.onCowByIdLoaded(cowEntity);
    }
}
