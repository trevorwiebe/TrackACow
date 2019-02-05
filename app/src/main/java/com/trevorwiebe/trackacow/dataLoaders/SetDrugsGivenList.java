package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;

public class SetDrugsGivenList extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "SetDrugsGivenList";

    private ArrayList<DrugsGivenEntity> mDrugsGivenEntity;

    public SetDrugsGivenList(ArrayList<DrugsGivenEntity> drugsGivenEntities){
        this.mDrugsGivenEntity = drugsGivenEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntity);
        return null;
    }
}