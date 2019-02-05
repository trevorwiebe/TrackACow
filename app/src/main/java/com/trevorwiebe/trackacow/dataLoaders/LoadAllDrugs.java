package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;

import java.util.ArrayList;
import java.util.List;

public class LoadAllDrugs extends AsyncTask<Context, Void, ArrayList<DrugEntity>> {

    private OnAllDrugsLoaded mOnAllDrugsLoaded;

    public LoadAllDrugs(OnAllDrugsLoaded onAllDrugsLoaded){
        this.mOnAllDrugsLoaded = onAllDrugsLoaded;
    }

    public interface OnAllDrugsLoaded{
        void onAllDrugsLoaded(ArrayList<DrugEntity> drugObjects);
    }

    @Override
    protected ArrayList<DrugEntity> doInBackground(Context... contexts) {
        List<DrugEntity> drugEntities = AppDatabase.getAppDatabase(contexts[0]).drugDao().getDrugList();
        return (ArrayList<DrugEntity>) drugEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugEntity> drugObjects) {
        super.onPostExecute(drugObjects);
        mOnAllDrugsLoaded.onAllDrugsLoaded(drugObjects);
    }
}
