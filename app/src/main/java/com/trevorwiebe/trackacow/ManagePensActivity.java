package com.trevorwiebe.trackacow;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.DeleteCowsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.DeletePen;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingPen;
import com.trevorwiebe.trackacow.dataLoaders.InsertPen;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.dataLoaders.UpdatePenName;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class ManagePensActivity extends AppCompatActivity implements
        QueryAllPens.OnPensLoaded,
        UpdatePenName.OnPenNameUpdated,
        InsertPen.OnPenInserted {

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference mPenRef = mBaseRef.child(PenEntity.PEN_OBJECT);
    private ArrayList<PenEntity> mPenEntityList = new ArrayList<>();

    private RecyclerView mPensRv;
    private TextView mEmptyTv;
    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pens);

        mEmptyTv = findViewById(R.id.empty_pen_rv);
        mPensRv = findViewById(R.id.manage_pens_rv);
        mPensRv.setLayoutManager(new LinearLayoutManager(this));
        mPenRecyclerViewAdapter = new PenRecyclerViewAdapter(mPenEntityList, true, this);
        mPensRv.setAdapter(mPenRecyclerViewAdapter);
        FloatingActionButton managePensFab = findViewById(R.id.manage_pens_fab);
        managePensFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder addNewPen = new AlertDialog.Builder(ManagePensActivity.this);
                addNewPen.setTitle("Add new pen");
                View dialogEditText = LayoutInflater.from(ManagePensActivity.this).inflate(R.layout.dialog_edit_text, null);
                addNewPen.setView(dialogEditText);
                final EditText dialogEditTextEditText = dialogEditText.findViewById(R.id.dialog_edit_text_edit_text);
                addNewPen.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(dialogEditTextEditText.length() != 0){
                            String penName = dialogEditTextEditText.getText().toString();
                            if(isPenNameAvailable(penName, mPenEntityList)) {

                                DatabaseReference pushRef = mPenRef.push();
                                String key = pushRef.getKey();
                                PenEntity penEntity = new PenEntity(key, "", 0, "", penName, 0);

                                if(Utility.haveNetworkConnection(ManagePensActivity.this)){
                                    pushRef.setValue(penEntity);
                                }else{
                                    Utility.setNewDataToUpload(ManagePensActivity.this, true);

                                    HoldingPenEntity holdingPenEntity = new HoldingPenEntity();
                                    holdingPenEntity.setWhatHappened(Utility.INSERT_UPDATE);
                                    holdingPenEntity.setPenId(penEntity.getPenId());
                                    holdingPenEntity.setIsActive(penEntity.getIsActive());
                                    holdingPenEntity.setPenName(penEntity.getPenName());
                                    holdingPenEntity.setCustomerName(penEntity.getCustomerName());
                                    holdingPenEntity.setTotalHead(penEntity.getTotalHead());
                                    holdingPenEntity.setNotes(penEntity.getNotes());

                                    new InsertHoldingPen(holdingPenEntity).execute(ManagePensActivity.this);
                                }
                                new InsertPen(penEntity, ManagePensActivity.this).execute(ManagePensActivity.this);
                            }else{
                                Snackbar.make(view, "This name is already taken", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                addNewPen.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                addNewPen.show();
            }
        });

        mPensRv.addOnItemTouchListener(new ItemClickListener(this, mPensRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {
                final PenEntity selectedPenEntity = mPenEntityList.get(position);
                AlertDialog.Builder editPen = new AlertDialog.Builder(ManagePensActivity.this);
                editPen.setTitle("Edit pen");
                View dialogView = LayoutInflater.from(ManagePensActivity.this).inflate(R.layout.dialog_edit_text, null);
                final EditText editPenEditText = dialogView.findViewById(R.id.dialog_edit_text_edit_text);
                editPen.setView(dialogView);
                editPenEditText.setText(selectedPenEntity.getPenName());
                editPenEditText.setSelection(selectedPenEntity.getPenName().length());
                editPen.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(editPenEditText.length() == 0){
                            Snackbar.make(mPensRv, "Please fill the blank", Snackbar.LENGTH_LONG).show();
                        }else{
                            String updatedText = editPenEditText.getText().toString();
                            if(isPenNameAvailable(updatedText, mPenEntityList)) {
                                selectedPenEntity.setPenName(updatedText);
                                if(Utility.haveNetworkConnection(ManagePensActivity.this)){
                                    mPenRef.child(selectedPenEntity.getPenId()).setValue(selectedPenEntity);
                                }else{
                                    Utility.setNewDataToUpload(ManagePensActivity.this, true);

                                    HoldingPenEntity holdingPenEntity = new HoldingPenEntity();
                                    holdingPenEntity.setNotes(selectedPenEntity.getNotes());
                                    holdingPenEntity.setTotalHead(selectedPenEntity.getTotalHead());
                                    holdingPenEntity.setCustomerName(selectedPenEntity.getCustomerName());
                                    holdingPenEntity.setPenName(selectedPenEntity.getPenName());
                                    holdingPenEntity.setIsActive(selectedPenEntity.getIsActive());
                                    holdingPenEntity.setPenId(selectedPenEntity.getPenId());
                                    holdingPenEntity.setWhatHappened(Utility.INSERT_UPDATE);

                                    new InsertHoldingPen(holdingPenEntity).execute(ManagePensActivity.this);
                                }

                                new UpdatePenName(selectedPenEntity.getPenId(), updatedText, ManagePensActivity.this).execute(ManagePensActivity.this);

                            }else{
                                Snackbar.make(mPensRv, "Pen already taken", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                editPen.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                editPen.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Utility.haveNetworkConnection(ManagePensActivity.this)){
                            String id = selectedPenEntity.getPenId();
                            mPenRef.child(id).removeValue();

                            Query cowQuery = mBaseRef.child(CowEntity.COW).orderByChild(CowEntity.PEN_ID).equalTo(id);
                            cowQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{
                            Utility.setNewDataToUpload(ManagePensActivity.this, true);

                            HoldingPenEntity holdingPenEntity = new HoldingPenEntity();
                            holdingPenEntity.setNotes(selectedPenEntity.getNotes());
                            holdingPenEntity.setTotalHead(selectedPenEntity.getTotalHead());
                            holdingPenEntity.setCustomerName(selectedPenEntity.getCustomerName());
                            holdingPenEntity.setPenName(selectedPenEntity.getPenName());
                            holdingPenEntity.setIsActive(selectedPenEntity.getIsActive());
                            holdingPenEntity.setPenId(selectedPenEntity.getPenId());
                            holdingPenEntity.setWhatHappened(Utility.DELETE);

                            new InsertHoldingPen(holdingPenEntity).execute(ManagePensActivity.this);

                            // TODO: 3/2/2019 save all the cows in this pen to the holding database to delete later when you connected to the internet.
                        }

                        new DeletePen(selectedPenEntity).execute(ManagePensActivity.this);
                        new QueryAllPens(ManagePensActivity.this).execute(ManagePensActivity.this);

                        new DeleteCowsByPenId(selectedPenEntity.getPenId()).execute(ManagePensActivity.this);

                        // TODO: 3/2/2019 delete all the drugs give on all this pen when the pen is deleted.
                    }
                });
                editPen.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new QueryAllPens(this).execute(this);
    }

    @Override
    public void onPenInserted() {
        new QueryAllPens(this).execute(this);
    }

    @Override
    public void onPenNameUpdated() {
        new QueryAllPens(this).execute(this);
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntityList) {
        mPenEntityList = penEntityList;
        mPenRecyclerViewAdapter.swapData(mPenEntityList);
        if(mPenEntityList.size() > 0){
            mEmptyTv.setVisibility(View.INVISIBLE);
        }else{
            mEmptyTv.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPenNameAvailable(String penName, ArrayList<PenEntity> penList){
        for(int r=0; r<penList.size(); r++){
            PenEntity penEntity = penList.get(r);
            if(penEntity.getPenName().equals(penName))return false;
        }
        return true;
    }
}
