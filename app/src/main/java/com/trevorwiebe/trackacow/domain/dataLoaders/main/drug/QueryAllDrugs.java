package com.trevorwiebe.trackacow.domain.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;

import java.util.ArrayList;
import java.util.List;

@Deprecated(since="Use use-cases instead")
public class QueryAllDrugs extends AsyncTask<Context, Void, ArrayList<DrugEntity>> {

    private OnAllDrugsLoaded mOnAllDrugsLoaded;

    public QueryAllDrugs(OnAllDrugsLoaded onAllDrugsLoaded){
        this.mOnAllDrugsLoaded = onAllDrugsLoaded;
    }

    public interface OnAllDrugsLoaded{
        void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities);
    }

    @Override
    protected ArrayList<DrugEntity> doInBackground(Context... contexts) {
        List<DrugEntity> drugEntities = AppDatabase.getAppDatabase(contexts[0]).drugDao().getDrugList2();
        return (ArrayList<DrugEntity>) drugEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugEntity> drugEntities) {
        super.onPostExecute(drugEntities);
        mOnAllDrugsLoaded.onAllDrugsLoaded(drugEntities);
    }
}
