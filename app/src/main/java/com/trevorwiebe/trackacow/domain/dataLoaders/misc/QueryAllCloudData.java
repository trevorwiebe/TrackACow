package com.trevorwiebe.trackacow.domain.dataLoaders.misc;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.data.entities.CallEntity;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.data.entities.RationEntity;
import com.trevorwiebe.trackacow.data.entities.UserEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;

import java.util.ArrayList;

public class QueryAllCloudData {

    private static final String TAG = "QueryAllCloudData";

    private final DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private final ArrayList<CowEntity> mCowEntityUpdateList = new ArrayList<>();
    private final ArrayList<DrugEntity> mDrugEntityUpdateList = new ArrayList<>();
    private final ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList = new ArrayList<>();
    private final ArrayList<PenEntity> mPenEntityUpdateList = new ArrayList<>();
    private final ArrayList<LotEntity> mLotEntityList = new ArrayList<>();
    private final ArrayList<ArchivedLotEntity> mArchivedLotList = new ArrayList<>();
    private final ArrayList<UserEntity> mUserEntity = new ArrayList<>();
    private final ArrayList<LoadEntity> mLoadList = new ArrayList<>();
    private final ArrayList<CallEntity> mCallList = new ArrayList<>();
    private final ArrayList<FeedEntity> mFeedList = new ArrayList<>();
    private final ArrayList<RationEntity> mRationList = new ArrayList<>();

    public OnAllCloudDataLoaded onAllCloudDataLoaded;

    public QueryAllCloudData(OnAllCloudDataLoaded onAllCloudDataLoaded) {
        this.onAllCloudDataLoaded = onAllCloudDataLoaded;
    }

    public interface OnAllCloudDataLoaded {
        void onAllCloudDataLoaded(
                int resultCode,
                ArrayList<CowEntity> cowEntities,
                ArrayList<DrugEntity> drugEntities,
                ArrayList<DrugsGivenEntity> drugsGivenEntities,
                ArrayList<PenEntity> penEntities,
                ArrayList<LotEntity> lotEntities,
                ArrayList<ArchivedLotEntity> archivedLotEntities,
                ArrayList<LoadEntity> loadEntities,
                ArrayList<CallEntity> callEntities,
                ArrayList<FeedEntity> feedEntities,
                ArrayList<UserEntity> userEntities,
                ArrayList<RationEntity> rationEntities
        );
    }

    public void loadCloudData() {
        baseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null) {
                        switch (key) {
                            case CowEntity.COW:
                                for (DataSnapshot cowSnapshot : snapshot.getChildren()) {
                                    CowEntity cowEntity = cowSnapshot.getValue(CowEntity.class);
                                    if (cowEntity != null) {
                                        mCowEntityUpdateList.add(cowEntity);
                                    }
                                }
                                break;
                            case DrugEntity.DRUG_OBJECT:
                                for (DataSnapshot drugsSnapshot : snapshot.getChildren()) {
                                    DrugEntity drugEntity = drugsSnapshot.getValue(DrugEntity.class);
                                    if (drugEntity != null) {
                                        mDrugEntityUpdateList.add(drugEntity);
                                    }
                                }
                                break;
                            case PenEntity.PEN_OBJECT:
                                for (DataSnapshot penSnapshot : snapshot.getChildren()) {
                                    PenEntity penEntity = penSnapshot.getValue(PenEntity.class);
                                    if (penEntity != null) {
                                        mPenEntityUpdateList.add(penEntity);
                                    }
                                }
                                break;
                            case DrugsGivenEntity.DRUGS_GIVEN:
                                for (DataSnapshot drugsGivenSnapShot : snapshot.getChildren()) {
                                    DrugsGivenEntity drugsGivenEntity = drugsGivenSnapShot.getValue(DrugsGivenEntity.class);
                                    if (drugsGivenEntity != null) {
                                        mDrugsGivenEntityUpdateList.add(drugsGivenEntity);
                                    }
                                }
                                break;
                            case LotEntity.LOT:
                                for (DataSnapshot lotSnapshot : snapshot.getChildren()) {
                                    LotEntity lotEntity = lotSnapshot.getValue(LotEntity.class);
                                    if (lotEntity != null) {
                                        mLotEntityList.add(lotEntity);
                                    }
                                }
                                break;
                            case ArchivedLotEntity.ARCHIVED_LOT:
                                for (DataSnapshot archiveLotSnapShot : snapshot.getChildren()) {
                                    ArchivedLotEntity archivedLotEntity = archiveLotSnapShot.getValue(ArchivedLotEntity.class);
                                    if (archivedLotEntity != null) {
                                        mArchivedLotList.add(archivedLotEntity);
                                    }
                                }
                                break;
                            case LoadEntity.LOAD:
                                for (DataSnapshot loadSnapShot : snapshot.getChildren()) {
                                    LoadEntity loadEntity = loadSnapShot.getValue(LoadEntity.class);
                                    if (loadEntity != null) {
                                        mLoadList.add(loadEntity);
                                    }
                                }
                            case UserEntity.USER:
                                UserEntity userEntity = snapshot.getValue(UserEntity.class);
                                if (userEntity != null) {
                                    mUserEntity.add(userEntity);
                                }
                                break;
                            case "call":
                                for(DataSnapshot callSnapShot : snapshot.getChildren()){
                                    CallEntity callEntity = callSnapShot.getValue(CallEntity.class);
                                    if(callEntity != null){
                                        mCallList.add(callEntity);
                                    }
                                }
                                break;
//                            case "rations":
//                                for(DataSnapshot rationSnapShot : snapshot.getChildren()){
//                                    RationEntity rationEntity = rationSnapShot.getValue(RationEntity.class);
//                                    if(rationEntity != null){
//                                        mRationList.add(rationEntity);
//                                    }
//                                }
//                                break;
                            case FeedEntity.FEED:
                                for(DataSnapshot feedSnapShot : snapshot.getChildren()){
                                    FeedEntity feedEntity = feedSnapShot.getValue(FeedEntity.class);
                                    if(feedEntity != null){
                                        mFeedList.add(feedEntity);
                                    }
                                }
                                break;
                            default:
                                Log.e(TAG, "onDataChange: unknown snapshot key - " + key);
                                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.SUCCESS,
                                        mCowEntityUpdateList,
                                        mDrugEntityUpdateList,
                                        mDrugsGivenEntityUpdateList,
                                        mPenEntityUpdateList,
                                        mLotEntityList,
                                        mArchivedLotList,
                                        mLoadList,
                                        mCallList,
                                        mFeedList,
                                        mUserEntity,
                                        mRationList
                                );
                                break;
                        }
                    }
                }

                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.SUCCESS,
                        mCowEntityUpdateList,
                        mDrugEntityUpdateList,
                        mDrugsGivenEntityUpdateList,
                        mPenEntityUpdateList,
                        mLotEntityList,
                        mArchivedLotList,
                        mLoadList,
                        mCallList,
                        mFeedList,
                        mUserEntity,
                        mRationList
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.ERROR_FETCHING_DATA_FROM_CLOUD,
                        mCowEntityUpdateList,
                        mDrugEntityUpdateList,
                        mDrugsGivenEntityUpdateList,
                        mPenEntityUpdateList,
                        mLotEntityList,
                        mArchivedLotList,
                        mLoadList,
                        mCallList,
                        mFeedList,
                        mUserEntity,
                        mRationList
                );
            }
        });

    }
}
