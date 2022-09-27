package com.trevorwiebe.trackacow.domain.dataLoaders.main.call;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.entities.CallEntity;

public class QueryCallById extends AsyncTask<Context, Void, CallEntity> {

    private String callId;
    private OnCallByIdLoaded onCallByIdLoaded;

    public QueryCallById(OnCallByIdLoaded onCallByIdLoaded) {
        this.onCallByIdLoaded = onCallByIdLoaded;
    }

    public interface OnCallByIdLoaded {
        void onCallByIdLoaded(CallEntity callEntity);
    }

    @Override
    protected CallEntity doInBackground(Context... contexts) {
        return null;
//        return AppDatabase.getAppDatabase(contexts[0]).callDao().getCallEntity(callId);
    }

    @Override
    protected void onPostExecute(CallEntity callEntity) {
        super.onPostExecute(callEntity);
        onCallByIdLoaded.onCallByIdLoaded(callEntity);
    }
}
