package com.trevorwiebe.trackacow.dataLoaders;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.List;

public class InsertAllLocalChangeToCloud extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "InsertAllLocalChangeToC";

    private DatabaseReference baseRef;
    private OnAllLocalDbInsertedToCloud mOnAllLocalDbInsertedToCloud;

    public InsertAllLocalChangeToCloud(DatabaseReference baseRef, OnAllLocalDbInsertedToCloud onAllLocalDbInsertedToCloud){
        this.baseRef = baseRef;
        this.mOnAllLocalDbInsertedToCloud = onAllLocalDbInsertedToCloud;
    }

    public interface OnAllLocalDbInsertedToCloud{
        void onAllLocalDbInsertedToCloud();
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        Utility.setNewDataToUpload((Activity) contexts[0], false);


        // update drug entities
        List<HoldingDrugEntity> holdingDrugEntities = db.holdingDrugDao().getHoldingDrugList();
        for(int a=0; a<holdingDrugEntities.size(); a++){
            HoldingDrugEntity holdingDrugEntity = holdingDrugEntities.get(a);

            DrugEntity drugEntity = new DrugEntity(holdingDrugEntity.getDefaultAmount(), holdingDrugEntity.getDrugId(), holdingDrugEntity.getDrugName());

            switch (holdingDrugEntity.getWhatHappened()){
                case Utility.INSERT_UPDATE:
                    baseRef.child(DrugEntity.DRUG_OBJECT).child(drugEntity.getDrugId()).setValue(drugEntity);
                    break;
                case Utility.DELETE:
                    baseRef.child(DrugEntity.DRUG_OBJECT).child(drugEntity.getDrugId()).removeValue();
                    break;
                default:
                    break;
            }
        }
        db.holdingDrugDao().deleteHoldingDrugTable();

        //update pen entities
        List<HoldingPenEntity> holdingPenEntities = db.holdingPenDao().getHoldingPenList();
        for(int b=0; b<holdingPenEntities.size(); b++){
            HoldingPenEntity holdingPenEntity = holdingPenEntities.get(b);

            PenEntity penEntity = new PenEntity(holdingPenEntity.getPenId(), holdingPenEntity.getCustomerName(), holdingPenEntity.getIsActive(), holdingPenEntity.getNotes(), holdingPenEntity.getPenName(), holdingPenEntity.getTotalHead());

            switch (holdingPenEntity.getWhatHappened()){
                case Utility.INSERT_UPDATE:
                    baseRef.child(PenEntity.PEN_OBJECT).child(penEntity.getPenId()).setValue(penEntity);
                    break;
                case Utility.DELETE:
                    baseRef.child(PenEntity.PEN_OBJECT).child(penEntity.getPenId()).removeValue();
                    break;
                default:
                        break;
            }
        }
        db.holdingPenDao().deleteHoldingPenTable();

        //update cow entities
        List<HoldingCowEntity> holdingCowEntities = db.holdingCowDao().getHoldingCowEntityList();
        for(int c=0; c<holdingCowEntities.size(); c++){

            HoldingCowEntity holdingCowEntity = holdingCowEntities.get(c);

            CowEntity cowEntity = new CowEntity(holdingCowEntity.isAlive(), holdingCowEntity.getCowId(), holdingCowEntity.getTagNumber(), holdingCowEntity.getDate(), holdingCowEntity.getNotes(), holdingCowEntity.getPenId());

            switch (holdingCowEntity.getWhatHappened()){
                case Utility.INSERT_UPDATE:
                    baseRef.child(CowEntity.COW).child(cowEntity.getCowId()).setValue(cowEntity);
                    break;
                case Utility.DELETE:
                    baseRef.child(CowEntity.COW).child(cowEntity.getCowId()).removeValue();
                    break;
                default:
                    break;
            }
        }
        db.holdingCowDao().deleteHoldingCowTable();

        // update drugs given entities
        List<HoldingDrugsGivenEntity> holdingDrugsGivenEntities = db.holdingDrugsGivenDao().getHoldingDrugsGivenList();
        for(int d=0; d<holdingDrugsGivenEntities.size(); d++){

            HoldingDrugsGivenEntity holdingDrugsGivenEntity = holdingDrugsGivenEntities.get(d);

            DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity(holdingDrugsGivenEntity.getDrugGivenId(), holdingDrugsGivenEntity.getDrugId(), holdingDrugsGivenEntity.getAmountGiven(), holdingDrugsGivenEntity.getCowId(), holdingDrugsGivenEntity.getPenId());

            switch (holdingDrugsGivenEntity.getWhatHappened()){
                case Utility.INSERT_UPDATE:
                    baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                    break;
                case Utility.DELETE:
                    baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).removeValue();
                    break;
            }
        }
        db.holdingDrugsGivenDao().deleteHoldingDrugsGivenTable();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnAllLocalDbInsertedToCloud.onAllLocalDbInsertedToCloud();
    }
}
