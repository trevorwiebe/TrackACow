package com.trevorwiebe.trackacow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.adapters.CustomerRecyclerViewAdapter;
import com.trevorwiebe.trackacow.objects.CustomerObject;

import java.util.ArrayList;

public class ManageCustomersActivity extends AppCompatActivity {

    private CustomerRecyclerViewAdapter mCustomerRecyclerViewAdapter;
    private ArrayList<CustomerObject> mCustList = new ArrayList<>();
    private DatabaseReference mCustRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(CustomerObject.CUSTOMER_OBJECT);
    private ValueEventListener mCustListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customers);

        FloatingActionButton addCustomerFab = findViewById(R.id.add_new_customer);
        RecyclerView customersRv = findViewById(R.id.manage_customers_rv);
        customersRv.setLayoutManager(new LinearLayoutManager(this));
        mCustomerRecyclerViewAdapter = new CustomerRecyclerViewAdapter(mCustList, this);
        customersRv.setAdapter(mCustomerRecyclerViewAdapter);

        addCustomerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewCustIntent = new Intent(ManageCustomersActivity.this, AddNewCustomer.class);
                startActivity(addNewCustIntent);
            }
        });

        mCustListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCustList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CustomerObject customerObject = snapshot.getValue(CustomerObject.class);
                    if(customerObject != null){
                        mCustList.add(customerObject);
                    }
                }
                mCustomerRecyclerViewAdapter.swapData(mCustList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustRef.addValueEventListener(mCustListener);
    }

    @Override
    protected void onPause() {
        mCustRef.removeEventListener(mCustListener);
        super.onPause();
    }
}
