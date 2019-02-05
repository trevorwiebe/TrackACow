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
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;

import java.util.ArrayList;

public class ManagePensActivity extends AppCompatActivity {

    private static final String TAG = "ManagePensActivity";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference mPenRef = mBaseRef.child(PenEntity.PEN_OBJECT);
    private ValueEventListener mPenListener;
    private ArrayList<PenEntity> mPenObjectList = new ArrayList<>();

    private RecyclerView mPensRv;
    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pens);

        final TextView emptyRvText = findViewById(R.id.empty_pen_rv);
        final ProgressBar loadingPens = findViewById(R.id.loading_pens);
        mPensRv = findViewById(R.id.manage_pens_rv);
        mPensRv.setLayoutManager(new LinearLayoutManager(this));
        mPenRecyclerViewAdapter = new PenRecyclerViewAdapter(mPenObjectList, true, this);
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
                        if(dialogEditTextEditText.length() == 0){

                        }else{
                            String penName = dialogEditTextEditText.getText().toString();
                            if(isPenNameAvailable(penName, mPenObjectList)) {
                                DatabaseReference pushRef = mPenRef.push();
                                String key = pushRef.getKey();
                                PenEntity penEntity = new PenEntity(key, "", 0, "", penName, 0);
                                pushRef.setValue(penEntity);
                                Snackbar.make(view, "Pen save successfully", Snackbar.LENGTH_LONG).show();
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

        mPenListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPenObjectList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PenEntity penEntity = snapshot.getValue(PenEntity.class);
                    if(penEntity != null){
                        mPenObjectList.add(penEntity);
                    }
                }
                loadingPens.setVisibility(View.INVISIBLE);
                mPenRecyclerViewAdapter.swapData(mPenObjectList);
                if(mPenObjectList.size() > 0){
                    emptyRvText.setVisibility(View.INVISIBLE);
                }else{
                    emptyRvText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mPensRv.addOnItemTouchListener(new ItemClickListener(this, mPensRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {
                final PenEntity selectedPenEntity = mPenObjectList.get(position);
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
                            if(isPenNameAvailable(updatedText, mPenObjectList)) {
                                selectedPenEntity.setPenName(updatedText);
                                mPenRef.child(selectedPenEntity.getPenId()).setValue(selectedPenEntity);
                                Snackbar.make(mPensRv, "Pen updated successfully", Snackbar.LENGTH_LONG).show();
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
                        String id = selectedPenEntity.getPenId();
                        mPenRef.child(id).removeValue();
                        Snackbar.make(view, "Pen deleted successfully", Snackbar.LENGTH_LONG).show();
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
        mPenRef.addValueEventListener(mPenListener);
    }

    @Override
    protected void onPause() {
        mPenRef.removeEventListener(mPenListener);
        super.onPause();
    }

    private boolean isPenNameAvailable(String penName, ArrayList<PenEntity> penList){
        for(int r=0; r<penList.size(); r++){
            PenEntity penEntity = penList.get(r);
            if(penEntity.getPenName().equals(penName))return false;
        }
        return true;
    }
}
