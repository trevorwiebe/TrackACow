package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;

import java.util.ArrayList;
import java.util.List;

public class LoadMedicatedCowsByPenId extends AsyncTask<Context, Void, ArrayList<CowEntity>> {

    private ArrayList<CowEntity> mCowObjectList = new ArrayList<>();
    private String mPenId;
    private OnCowsLoaded mOnCowsLoaded;

    public LoadMedicatedCowsByPenId(OnCowsLoaded onCowsLoaded, String penId){
        this.mPenId = penId;
        this.mOnCowsLoaded = onCowsLoaded;
    }

    public interface OnCowsLoaded{
        void onCowsLoaded(ArrayList<CowEntity> cowObjectList);
    }

    @Override
    protected ArrayList<CowEntity> doInBackground(Context... contexts) {
        List<CowEntity> cowEntities = AppDatabase.getAppDatabase(contexts[0]).cowDao().getCowEntitiesByPenId(mPenId);
        return (ArrayList<CowEntity>) cowEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<CowEntity> cowObjects) {
        super.onPostExecute(cowObjects);
        mOnCowsLoaded.onCowsLoaded(cowObjects);
    }
}
