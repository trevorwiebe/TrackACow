package com.trevorwiebe.trackacow.domain.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;

@Deprecated(since="Use use-cases instead")
public class DeleteDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public DeleteDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        String drugId = drugEntity.getDrugCloudDatabaseId();
        AppDatabase.getAppDatabase(contexts[0]).drugDao().deleteDrugById(drugId);
        return null;
    }
}
