package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

public class UpdatePen extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "UpdatePen";

    private PenEntity penEntity;

    public UpdatePen(PenEntity penEntity){
        this.penEntity = penEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
//        String customerName = penEntity.getCustomerName();
//        int totalHead = penEntity.getTotalHead();
//        String notes = penEntity.getNotes();
//        String penId = penEntity.getPenId();
//        int isActive = penEntity.getIsActive();
//        AppDatabase.getAppDatabase(contexts[0]).penDao().updatePenByFields(customerName, isActive, totalHead, notes, penId);
        return null;
    }
}