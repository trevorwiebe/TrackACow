package com.trevorwiebe.trackacow.domain.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.local.entities.DrugEntity;

public class InsertDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public InsertDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugDao().insertDrug(drugEntity);
        return null;
    }
}
