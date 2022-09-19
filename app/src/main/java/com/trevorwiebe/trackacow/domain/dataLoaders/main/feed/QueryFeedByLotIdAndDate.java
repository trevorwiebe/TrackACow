package com.trevorwiebe.trackacow.domain.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.FeedEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryFeedByLotIdAndDate extends AsyncTask<Context, Void, ArrayList<FeedEntity>> {

    private String lotId;
    private long date;
    private OnFeedByLotIdAndDateLoaded onFeedByLotIdAndDateLoaded;

    public QueryFeedByLotIdAndDate(long date, String lotId, OnFeedByLotIdAndDateLoaded onFeedByLotIdAndDateLoaded) {
        this.lotId = lotId;
        this.date = date;
        this.onFeedByLotIdAndDateLoaded = onFeedByLotIdAndDateLoaded;
    }

    public interface OnFeedByLotIdAndDateLoaded {
        void onFeedByLotIdAndDateLoaded(ArrayList<FeedEntity> feedEntities);
    }

    @Override
    protected ArrayList<FeedEntity> doInBackground(Context... contexts) {
        List<FeedEntity> feedEntities = AppDatabase.getAppDatabase(contexts[0]).feedDao().getFeedEntitiesByLotAndDate(lotId, date);
        return (ArrayList<FeedEntity>) feedEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedEntity> feedEntities) {
        super.onPostExecute(feedEntities);
        onFeedByLotIdAndDateLoaded.onFeedByLotIdAndDateLoaded(feedEntities);
    }
}
