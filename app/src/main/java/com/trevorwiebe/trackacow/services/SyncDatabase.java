package com.trevorwiebe.trackacow.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class SyncDatabase extends JobService implements
        InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud,
        CloneCloudDatabaseToLocalDatabase.OnDatabaseCloned {

    private static final String TAG = "SyncDatabase";

    private ArrayList<CowEntity> mCowEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList = new ArrayList<>();
    private ArrayList<PenEntity> mPenEntityUpdateList = new ArrayList<>();

    private DatabaseReference mBaseRef;

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Utility.setNewDataToUpload(this, false);

            mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            new InsertAllLocalChangeToCloud(mBaseRef, this).execute(this);

            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        String channelId = getResources().getString(R.string.sync_notif_channel_id);
        Utility.showNotification(this, channelId, "Database synced", "Data synced successfully");
        return false;
    }

    @Override
    public void onAllLocalDbInsertedToCloud() {
        mBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null) {
                        switch (key) {
                            case "cows":
                                for (DataSnapshot cowSnapshot : snapshot.getChildren()) {
                                    CowEntity cowEntity = cowSnapshot.getValue(CowEntity.class);
                                    if (cowEntity != null) {
                                        mCowEntityUpdateList.add(cowEntity);
                                    }
                                }
                                break;
                            case "drugs":
                                for (DataSnapshot drugsSnapshot : snapshot.getChildren()) {
                                    DrugEntity drugEntity = drugsSnapshot.getValue(DrugEntity.class);
                                    if (drugEntity != null) {
                                        mDrugEntityUpdateList.add(drugEntity);
                                    }
                                }
                                break;
                            case "pens":
                                for (DataSnapshot penSnapshot : snapshot.getChildren()) {
                                    PenEntity penEntity = penSnapshot.getValue(PenEntity.class);
                                    if (penEntity != null) {
                                        mPenEntityUpdateList.add(penEntity);
                                    }
                                }
                                break;
                            case "drugsGiven":
                                for (DataSnapshot drugsGivenSnapShot : snapshot.getChildren()) {
                                    DrugsGivenEntity drugsGivenEntity = drugsGivenSnapShot.getValue(DrugsGivenEntity.class);
                                    if (drugsGivenEntity != null) {
                                        mDrugsGivenEntityUpdateList.add(drugsGivenEntity);
                                    }
                                }
                                break;
                            default:
                                Log.e(TAG, "onDataChange: unknown snapshot key");
                        }
                    }
                }

                new CloneCloudDatabaseToLocalDatabase(SyncDatabase.this, mCowEntityUpdateList, mDrugEntityUpdateList, mDrugsGivenEntityUpdateList, mPenEntityUpdateList).execute(getApplicationContext());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDatabaseCloned() {

    }
}
