package com.trevorwiebe.trackacow.domain.dataLoaders.misc;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.data.entities.UserEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingArchivedLotEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingFeedEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingUserEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

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

        if (!Utility.haveNetworkConnection(contexts[0])) {
            return Constants.NO_NETWORK_CONNECTION;
        }

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        try {
            // update drug entities
            List<HoldingDrugEntity> holdingDrugEntities = db.holdingDrugDao().getHoldingDrugList();
            for (int a = 0; a < holdingDrugEntities.size(); a++) {
                HoldingDrugEntity holdingDrugEntity = holdingDrugEntities.get(a);

                DrugEntity drugEntity = new DrugEntity(holdingDrugEntity.getDefaultAmount(), holdingDrugEntity.getDrugId(), holdingDrugEntity.getDrugName());

                switch (holdingDrugEntity.getWhatHappened()) {
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
            for (int b = 0; b < holdingPenEntities.size(); b++) {
                HoldingPenEntity holdingPenEntity = holdingPenEntities.get(b);

                PenEntity penEntity = new PenEntity(holdingPenEntity.getPenId(), holdingPenEntity.getPenName());

                switch (holdingPenEntity.getWhatHappened()) {
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
            for (int c = 0; c < holdingCowEntities.size(); c++) {

                HoldingCowEntity holdingCowEntity = holdingCowEntities.get(c);

                CowEntity cowEntity = new CowEntity(holdingCowEntity.isAlive(), holdingCowEntity.getCowId(), holdingCowEntity.getTagNumber(), holdingCowEntity.getDate(), holdingCowEntity.getNotes(), holdingCowEntity.getLotId());

                switch (holdingCowEntity.getWhatHappened()) {
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
            for (int d = 0; d < holdingDrugsGivenEntities.size(); d++) {

                HoldingDrugsGivenEntity holdingDrugsGivenEntity = holdingDrugsGivenEntities.get(d);

                DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity(holdingDrugsGivenEntity.getDrugGivenId(), holdingDrugsGivenEntity.getDrugId(), holdingDrugsGivenEntity.getAmountGiven(), holdingDrugsGivenEntity.getCowId(), holdingDrugsGivenEntity.getLotId(), holdingDrugsGivenEntity.getDate());

                switch (holdingDrugsGivenEntity.getWhatHappened()) {
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

            // update archive lot entities
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

            // update load entities
            List<HoldingLoadEntity> holdingLoadEntities = db.holdingLoadDao().getHoldingLoadList();
            for (int h = 0; h < holdingLoadEntities.size(); h++) {

                HoldingLoadEntity holdingLoadEntity = holdingLoadEntities.get(h);

                LoadEntity loadEntity = new LoadEntity(holdingLoadEntity);
                switch (holdingLoadEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(LoadEntity.LOAD).child(loadEntity.getLoadId()).setValue(loadEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(LoadEntity.LOAD).child(loadEntity.getLoadId()).removeValue();
                        break;
                }
            }
            db.holdingLoadDao().deleteHoldingLoadTable();

            // update callEntity node
//            List<HoldingCallEntity> holdingCallEntities = db.holdingCallDao().getHoldingCallEntities();
//            for (int j=0; j<holdingCallEntities.size(); j++){
//                HoldingCallEntity holdingCallEntity = holdingCallEntities.get(j);
//                CallEntity callEntity = new CallEntity(holdingCallEntity);
//
//                switch (holdingCallEntity.getWhatHappened()){
//                    case Constants.INSERT_UPDATE:
//                        baseRef.child(CallEntity.CALL).child(callEntity.getId()).setValue(callEntity);
//                        break;
//                    case Constants.DELETE:
//                        baseRef.child(CallEntity.CALL).child(callEntity.getId()).removeValue();
//                        break;
//                }
//            }
//            db.holdingCallDao().deleteCallTable();

            // update feedEntity node
            List<HoldingFeedEntity> holdingFeedEntities = db.holdingFeedDao().getHoldingFeedEntities();
            for(int f=0; f<holdingFeedEntities.size(); f++){
                HoldingFeedEntity holdingFeedEntity = holdingFeedEntities.get(f);
                FeedEntity feedEntity = new FeedEntity(holdingFeedEntity);

                switch (holdingFeedEntity.getWhatHappened()){
                    case Constants.INSERT_UPDATE:
                        baseRef.child(FeedEntity.FEED).child(feedEntity.getId()).setValue(feedEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(FeedEntity.FEED).child(feedEntity.getId()).removeValue();
                        break;
                }
            }
            db.holdingFeedDao().deleteHoldingFeedTable();

            // update user node
            List<HoldingUserEntity> holdingUserEntities = db.holdingUserDao().getHoldingUserList();
            for (int g = 0; g < holdingUserEntities.size(); g++) {

                HoldingUserEntity holdingUserEntity = holdingUserEntities.get(g);

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
            db.holdingUserDao().deleteHoldingUserTable();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return Constants.ERROR_PUSHING_DATA_TO_CLOUD;
        }

        return Constants.SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        super.onPostExecute(resultCode);
        mOnAllLocalDbInsertedToCloud.onAllLocalDbInsertedToCloud(resultCode);
    }
}
