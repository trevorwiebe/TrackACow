package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.objects.CowObject;

import java.util.ArrayList;
import java.util.List;

public class LoadMedicatedCowsByPenId extends AsyncTask<Context, Void, ArrayList<CowObject>> {

    private ArrayList<CowObject> mCowObjectList = new ArrayList<>();
    private String mPenId;
    private OnCowsLoaded mOnCowsLoaded;

    public LoadMedicatedCowsByPenId(OnCowsLoaded onCowsLoaded, String penId){
        this.mPenId = penId;
        this.mOnCowsLoaded = onCowsLoaded;
    }

    public interface OnCowsLoaded{
        void onCowsLoaded(ArrayList<CowObject> cowObjectList);
    }

    @Override
    protected ArrayList<CowObject> doInBackground(Context... contexts) {
        List<CowEntity> cowEntities = AppDatabase.getAppDatabase(contexts[0]).cowDao().getCowEntitiesByPenId(mPenId);
        for(int x=0; x<cowEntities.size(); x++){
            CowEntity cowEntity = cowEntities.get(x);
            CowObject cowObject = new CowObject(cowEntity.getTagNumber(), cowEntity.getCowId(), cowEntity.getPenId(), cowEntity.getNotes(), cowEntity.isAlive(), cowEntity.getDate(), null);
            mCowObjectList.add(cowObject);
        }
        return mCowObjectList;
    }

    @Override
    protected void onPostExecute(ArrayList<CowObject> cowObjects) {
        super.onPostExecute(cowObjects);
        mOnCowsLoaded.onCowsLoaded(cowObjects);
    }
}
