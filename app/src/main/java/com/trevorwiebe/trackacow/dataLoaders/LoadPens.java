package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;
import java.util.List;

public class LoadPens extends AsyncTask<Context, Void, ArrayList<PenEntity>> {

    private static final String TAG = "LoadPens";

    private OnPensLoaded mOnPensLoaded;

    public interface OnPensLoaded{
        void onPensLoaded(ArrayList<PenEntity> penObjectList);
    }

    public LoadPens(OnPensLoaded onPensLoaded){
        this.mOnPensLoaded = onPensLoaded;
    }

    @Override
    protected ArrayList<PenEntity> doInBackground(Context... contexts) {
        List<PenEntity> penEntities = AppDatabase.getAppDatabase(contexts[0]).penDao().getPenList();
        Log.d(TAG, "doInBackground: " + penEntities.size());
        return (ArrayList<PenEntity>) penEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<PenEntity> penObjects) {
        super.onPostExecute(penObjects);
        mOnPensLoaded.onPensLoaded(penObjects);
    }
}
