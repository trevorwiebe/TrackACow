package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.objects.PenObject;

import java.util.ArrayList;
import java.util.List;

public class LoadPensAsyncTask extends AsyncTask<Context, Void, ArrayList<PenObject>> {

    private static final String TAG = "LoadPensAsyncTask";

    private ArrayList<PenObject> penList = new ArrayList<>();
    private OnPensLoaded mOnPensLoaded;

    public interface OnPensLoaded{
        void onPensLoaded(ArrayList<PenObject> penObjectList);
    }

    public LoadPensAsyncTask(OnPensLoaded onPensLoaded){
        this.mOnPensLoaded = onPensLoaded;
    }

    @Override
    protected ArrayList<PenObject> doInBackground(Context... contexts) {
        List<PenEntity> penEntityList = AppDatabase.getAppDatabase(contexts[0]).penDao().getPenList();
        Log.d(TAG, "doInBackground: " + penEntityList.toString());
        for(int i=0; i<penEntityList.size(); i++){
            PenEntity penEntity = penEntityList.get(i);
            PenObject penObject = new PenObject(penEntity.getPenDatabaseId(), penEntity.getPenName(), penEntity.getCustomerName(), penEntity.getTotalHead(), penEntity.getNotes(), penEntity.getIsActive());
            penList.add(penObject);
        }
        return penList;
    }

    @Override
    protected void onPostExecute(ArrayList<PenObject> penObjects) {
        super.onPostExecute(penObjects);
        mOnPensLoaded.onPensLoaded(penObjects);
    }
}
