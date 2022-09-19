package com.trevorwiebe.trackacow.domain.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.trackacow.data.db.AppDatabase;

public class DeleteFeedEntitiesByDateAndLotId extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "DeleteFeedEntitiesByDat";

    private long date;
    private String lotId;
    private OnFeedEntitiesByDateAndLotIdDeleted onFeedEntitiesByDateAndLotIdDeleted;

    public DeleteFeedEntitiesByDateAndLotId(long date, String lotId, OnFeedEntitiesByDateAndLotIdDeleted onFeedEntitiesByDateAndLotIdDeleted) {
        this.date = date;
        this.lotId = lotId;
        this.onFeedEntitiesByDateAndLotIdDeleted = onFeedEntitiesByDateAndLotIdDeleted;
    }

    public interface OnFeedEntitiesByDateAndLotIdDeleted {
        void onFeedEntitiesByDateAndLotIdDeleted();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        int numsDeleted = AppDatabase.getAppDatabase(contexts[0]).feedDao().deleteFeedEntitiesByDateAndLotId(date, lotId);
        Log.d(TAG, "doInBackground: " + numsDeleted);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFeedEntitiesByDateAndLotIdDeleted.onFeedEntitiesByDateAndLotIdDeleted();
    }
}
