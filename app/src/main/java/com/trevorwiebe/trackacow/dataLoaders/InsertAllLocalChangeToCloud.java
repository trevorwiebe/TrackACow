package com.trevorwiebe.trackacow.dataLoaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.List;

public class InsertAllLocalChangeToCloud extends AsyncTask<Context, Void, Integer> {

    private static final String TAG = "InsertAllLocalChangeToC";

    private DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private OnAllLocalDbInsertedToCloud mOnAllLocalDbInsertedToCloud;

    public InsertAllLocalChangeToCloud(OnAllLocalDbInsertedToCloud onAllLocalDbInsertedToCloud) {
        this.mOnAllLocalDbInsertedToCloud = onAllLocalDbInsertedToCloud;
    }

    public interface OnAllLocalDbInsertedToCloud{
        void onAllLocalDbInsertedToCloud(int resultCode);
    }

    @Override
    protected Integer doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        // update drug entities
        List<HoldingDrugEntity> holdingDrugEntities = db.holdingDrugDao().getHoldingDrugList();
        for(int a=0; a<holdingDrugEntities.size(); a++){
            HoldingDrugEntity holdingDrugEntity = holdingDrugEntities.get(a);

            DrugEntity drugEntity = new DrugEntity(holdingDrugEntity.getDefaultAmount(), holdingDrugEntity.getDrugId(), holdingDrugEntity.getDrugName());

            switch (holdingDrugEntity.getWhatHappened()){
                case Constants.INSERT_UPDATE:
                    baseRef.child(DrugEntity.DRUG_OBJECT).child(drugEntity.getDrugId()).setValue(drugEntity);
                    break;
                case Constants.DELETE:
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

            PenEntity penEntity = new PenEntity(holdingPenEntity.getPenId(), holdingPenEntity.getPenName());

            switch (holdingPenEntity.getWhatHappened()){
                case Constants.INSERT_UPDATE:
                    baseRef.child(PenEntity.PEN_OBJECT).child(penEntity.getPenId()).setValue(penEntity);
                    break;
                case Constants.DELETE:
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

            CowEntity cowEntity = new CowEntity(holdingCowEntity.isAlive(), holdingCowEntity.getCowId(), holdingCowEntity.getTagNumber(), holdingCowEntity.getDate(), holdingCowEntity.getNotes(), holdingCowEntity.getLotId());

            switch (holdingCowEntity.getWhatHappened()){
                case Constants.INSERT_UPDATE:
                    baseRef.child(CowEntity.COW).child(cowEntity.getCowId()).setValue(cowEntity);
                    break;
                case Constants.DELETE:
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

            DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity(holdingDrugsGivenEntity.getDrugGivenId(), holdingDrugsGivenEntity.getDrugId(), holdingDrugsGivenEntity.getAmountGiven(), holdingDrugsGivenEntity.getCowId(), holdingDrugsGivenEntity.getLotId());

            switch (holdingDrugsGivenEntity.getWhatHappened()){
                case Constants.INSERT_UPDATE:
                    baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                    break;
                case Constants.DELETE:
                    baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).removeValue();
                    break;
            }
        }
        db.holdingDrugsGivenDao().deleteHoldingDrugsGivenTable();

        // update lot entities
        List<HoldingLotEntity> holdingLotEntities = db.holdingLotDao().getHoldingLotList();
        for (int e = 0; e < holdingLotEntities.size(); e++) {

            HoldingLotEntity holdingLotEntity = holdingLotEntities.get(e);

            LotEntity lotEntity = new LotEntity(holdingLotEntity);
            switch (holdingLotEntity.getWhatHappened()) {
                case Constants.INSERT_UPDATE:
                    baseRef.child(LotEntity.LOT).child(lotEntity.getLotId()).setValue(lotEntity);
                    break;
                case Constants.DELETE:
                    baseRef.child(LotEntity.LOT).child(lotEntity.getLotId()).removeValue();
                    break;
            }
        }
        db.holdingLotDao().deleteHoldingLotTable();


        List<HoldingArchivedLotEntity> holdingArchivedLotEntities = db.holdingArchivedLotDao().getHoldingArchivedLotList();
        for (int f = 0; f < holdingArchivedLotEntities.size(); f++) {

            HoldingArchivedLotEntity holdingArchivedLotEntity = holdingArchivedLotEntities.get(f);

            ArchivedLotEntity archivedLotEntity = new ArchivedLotEntity(holdingArchivedLotEntity);
            switch (holdingArchivedLotEntity.getWhatHappened()) {
                case Constants.INSERT_UPDATE:
                    baseRef.child(ArchivedLotEntity.ARCHIVED_LOT).child(archivedLotEntity.getLotId()).setValue(archivedLotEntity);
                    break;
                case Constants.DELETE:
                    baseRef.child(ArchivedLotEntity.ARCHIVED_LOT).child(archivedLotEntity.getLotId()).removeValue();
                    break;
            }
        }
        db.holdingArchivedLotDao().deleteHoldingArchivedLotTable();

        List<HoldingUserEntity> holdingUserEntities = db.holdingUserDao().getHoldingUserList();
        Log.d(TAG, "doInBackground: " + holdingUserEntities.size());
        for (int g = 0; g < holdingUserEntities.size(); g++) {

            HoldingUserEntity holdingUserEntity = holdingUserEntities.get(g);

            Log.d(TAG, "doInBackground: " + holdingUserEntity);

            UserEntity userEntity = new UserEntity(holdingUserEntity);
            switch (holdingUserEntity.getWhatHappened()) {
                case Constants.INSERT_UPDATE:
                    baseRef.child(UserEntity.USER).setValue(userEntity);
                    break;
                case Constants.DELETE:
                    baseRef.child(UserEntity.USER).removeValue();
                    break;
            }
        }
//        db.holdingUserDao().deleteHoldingUserTable();

        return Constants.SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        super.onPostExecute(resultCode);
        mOnAllLocalDbInsertedToCloud.onAllLocalDbInsertedToCloud(resultCode);
    }
}
