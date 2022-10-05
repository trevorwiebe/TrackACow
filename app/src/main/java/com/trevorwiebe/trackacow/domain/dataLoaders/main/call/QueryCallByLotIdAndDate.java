package com.trevorwiebe.trackacow.domain.dataLoaders.main.call;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.entities.CallEntity;

@Deprecated(since="Use use-cases instead")
public class QueryCallByLotIdAndDate extends AsyncTask<Context, Void, CallEntity> {

    private long date;
    private String lotId;
    private OnCallByLotIdAndDateLoaded onCallByLotIdAndDateLoaded;

    public QueryCallByLotIdAndDate(long date, String lotId, OnCallByLotIdAndDateLoaded onCallByLotIdAndDateLoaded) {
        this.date = date;
        this.lotId = lotId;
        this.onCallByLotIdAndDateLoaded = onCallByLotIdAndDateLoaded;
    }

    public interface OnCallByLotIdAndDateLoaded {
        void onCallByLotIdAndDateLoaded(CallEntity callEntity);
    }

    @Override
    protected CallEntity doInBackground(Context... contexts) {
        return null;
//        return AppDatabase.getAppDatabase(contexts[0]).callDao().getCallEntityByDateAndLotId(date, lotId);
    }

    @Override
    protected void onPostExecute(CallEntity callEntity) {
        super.onPostExecute(callEntity);
        onCallByLotIdAndDateLoaded.onCallByLotIdAndDateLoaded(callEntity);
    }
}
