package com.trevorwiebe.trackacow.domain.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;

@Deprecated(since="Use use-cases instead")
public class UpdateDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public UpdateDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        int defaultAmount = drugEntity.getDefaultAmount();
        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugCloudDatabaseId();
        AppDatabase.getAppDatabase(contexts[0]).drugDao().updateDrugById(defaultAmount, drugName, drugId);
        return null;
    }

}
