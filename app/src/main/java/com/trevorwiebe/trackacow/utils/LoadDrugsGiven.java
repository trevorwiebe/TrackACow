package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;

import java.util.ArrayList;
import java.util.List;

public class LoadDrugsGiven extends AsyncTask<Context, Void, ArrayList<DrugsGivenObject>> {

    private ArrayList<DrugsGivenObject> mDrugsGivenObjects = new ArrayList<>();
    private OnDrugsLoaded mOnDrugsLoaded;

    public LoadDrugsGiven(OnDrugsLoaded onDrugsLoaded){
        this.mOnDrugsLoaded = onDrugsLoaded;
    }

    public interface OnDrugsLoaded{
        void onDrugsLoaded(ArrayList<DrugsGivenObject> drugsGivenObjects);
    }

    @Override
    protected ArrayList<DrugsGivenObject> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenList();
        for(int o=0; o<drugsGivenEntities.size(); o++){
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(o);
            DrugsGivenObject drugsGivenObject = new DrugsGivenObject("drugId", drugsGivenEntity.getAmountGiven(), drugsGivenEntity.getDate(), drugsGivenEntity.getCowId());
            mDrugsGivenObjects.add(drugsGivenObject);
        }
        return mDrugsGivenObjects;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenObject> drugsGivenObjects) {
        super.onPostExecute(drugsGivenObjects);
        mOnDrugsLoaded.onDrugsLoaded(mDrugsGivenObjects);
    }
}
