package com.trevorwiebe.trackacow.domain.dataLoaders.main.call;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.CallEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryCallsByLotId extends AsyncTask<Context, Void, ArrayList<CallEntity>> {

    private String lotId;
    private OnCallsByLotIdReturned onCallsByLotIdReturned;

    public QueryCallsByLotId(String lotId, OnCallsByLotIdReturned onCallsByLotIdReturned) {
        this.lotId = lotId;
        this.onCallsByLotIdReturned = onCallsByLotIdReturned;
    }

    public interface OnCallsByLotIdReturned {
        void onCallsByLotIdReturned(ArrayList<CallEntity> callEntities);
    }

    @Override
    protected ArrayList<CallEntity> doInBackground(Context... contexts) {
        List<CallEntity> callEntities = AppDatabase.getAppDatabase(contexts[0]).callDao().getCallEntitiesByLotId(lotId);
        return (ArrayList<CallEntity>) callEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<CallEntity> callEntities) {
        super.onPostExecute(callEntities);
        onCallsByLotIdReturned.onCallsByLotIdReturned(callEntities);
    }

}
