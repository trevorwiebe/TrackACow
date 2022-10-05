package com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;

import java.util.List;

@Deprecated(since="Use use-cases instead")
public class InsertDrugsGivenList extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "InsertDrugsGivenList";

    private List<DrugsGivenEntity> mDrugsGivenEntity;

    public InsertDrugsGivenList(List<DrugsGivenEntity> drugsGivenEntities){
        this.mDrugsGivenEntity = drugsGivenEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntity);
        return null;
    }
}