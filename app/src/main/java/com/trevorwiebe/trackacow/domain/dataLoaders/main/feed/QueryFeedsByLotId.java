package com.trevorwiebe.trackacow.domain.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryFeedsByLotId extends AsyncTask<Context, Void, ArrayList<FeedEntity>> {

    private String lotId;
    private OnFeedsByLotIdReturned onFeedsByLotIdReturned;

    public QueryFeedsByLotId(String lotId, OnFeedsByLotIdReturned onFeedsByLotIdReturned) {
        this.lotId = lotId;
        this.onFeedsByLotIdReturned = onFeedsByLotIdReturned;
    }

    public interface OnFeedsByLotIdReturned {
        void onFeedsByLotIdReturned(ArrayList<FeedEntity> feedEntities);
    }

    @Override
    protected ArrayList<FeedEntity> doInBackground(Context... contexts) {
        List<FeedEntity> feedEntities = AppDatabase.getAppDatabase(contexts[0]).feedDao().getFeedEntitiesByLotId(lotId);
        return (ArrayList<FeedEntity>) feedEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedEntity> feedEntities) {
        super.onPostExecute(feedEntities);
        onFeedsByLotIdReturned.onFeedsByLotIdReturned(feedEntities);
    }
}
