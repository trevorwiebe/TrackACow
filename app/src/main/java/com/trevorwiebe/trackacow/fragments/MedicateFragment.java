package com.trevorwiebe.trackacow.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.activities.MainActivity;
import com.trevorwiebe.trackacow.activities.MedicatedCowsActivity;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.dataLoaders.QueryLots;
import com.trevorwiebe.trackacow.dataLoaders.QueryUserEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class MedicateFragment extends Fragment implements
        QueryAllPens.OnPensLoaded,
        QueryLots.OnLotsLoaded {

    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;
    private ArrayList<PenEntity> mPenList = new ArrayList<>();
    private ArrayList<LotEntity> mLotList = new ArrayList<>();

    private static final String TAG = "MedicateFragment";

    private TextView mNoPensTv;
    private RecyclerView mPenRv;

    private Context mContext;

    public MedicateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_medicate, container, false);

        mNoPensTv = rootView.findViewById(R.id.no_pens_tv);
        mPenRv = rootView.findViewById(R.id.main_rv);
        mPenRv.setLayoutManager(new LinearLayoutManager(mContext));
        mPenRecyclerViewAdapter = new PenRecyclerViewAdapter(mPenList, mLotList, mContext);
        mPenRv.setAdapter(mPenRecyclerViewAdapter);

        mPenRv.addOnItemTouchListener(new ItemClickListener(mContext, mPenRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent trackCowIntent = new Intent(mContext, MedicatedCowsActivity.class);
                String penId = mPenList.get(position).getPenId();
                Utility.setPenId(mContext, penId);
                trackCowIntent.putExtra("penEntityId", penId);
                startActivity(trackCowIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onResume() {

        new QueryLots(MedicateFragment.this).execute(mContext);

        super.onResume();
    }


    @Override
    public void onLotsLoaded(ArrayList<LotEntity> lotEntities) {
        mLotList = lotEntities;
        new QueryAllPens(MedicateFragment.this).execute(mContext);
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        mPenList = penEntitiesList;
        setPenRecyclerView();
    }

    private void setPenRecyclerView() {
        if (mPenList.size() == 0) {
            mNoPensTv.setVisibility(View.VISIBLE);
        } else {
            mNoPensTv.setVisibility(View.INVISIBLE);
        }
        mPenRecyclerViewAdapter.swapData(mPenList, mLotList);
    }
}
