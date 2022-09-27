package com.trevorwiebe.trackacow.domain.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;

import java.util.List;

public class InsertFeedEntities extends AsyncTask<Context, Void, Void> {

    private List<FeedEntity> feedEntities;

    public InsertFeedEntities(List<FeedEntity> feedEntities) {
        this.feedEntities = feedEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).feedDao().insertFeedEntityList(feedEntities);
        return null;
    }
}
