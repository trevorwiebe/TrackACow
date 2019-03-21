package com.trevorwiebe.trackacow.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.ShufflePenAndLotsAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.dataLoaders.QueryLots;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.objects.ShuffleObject;
import com.trevorwiebe.trackacow.utils.DragHelper;

import java.util.ArrayList;

public class MoveFragment extends Fragment implements
        QueryAllPens.OnPensLoaded,
        QueryLots.OnLotsLoaded {

    private RecyclerView mMoveRv;

    private ArrayList<PenEntity> mPenEntities = new ArrayList<>();
    private ShufflePenAndLotsAdapter mShuffleAdapter;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.move_layout, container, false);

        mMoveRv = rootView.findViewById(R.id.shuffle_rv);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMoveRv.setLayoutManager(new LinearLayoutManager(mContext));

        mShuffleAdapter = new ShufflePenAndLotsAdapter();

        DragHelper dragHelper = new DragHelper(mShuffleAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragHelper);
        mShuffleAdapter.setTouchHelper(itemTouchHelper);

        mMoveRv.setAdapter(mShuffleAdapter);

        itemTouchHelper.attachToRecyclerView(mMoveRv);

        new QueryAllPens(MoveFragment.this).execute(mContext);
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        mPenEntities = penEntitiesList;
        new QueryLots(MoveFragment.this).execute(mContext);
    }

    @Override
    public void onLotsLoaded(ArrayList<LotEntity> lotEntities) {
        ArrayList<ShuffleObject> shuffleObjects = new ArrayList<>();
        for (int r = 0; r < mPenEntities.size(); r++) {

            PenEntity penEntity = mPenEntities.get(r);
            String penName = penEntity.getPenName();
            String penId = penEntity.getPenId();

            ShuffleObject penShuffleObject = new ShuffleObject(ShufflePenAndLotsAdapter.PEN_NAME, penName, penId);
            shuffleObjects.add(penShuffleObject);

            ArrayList<LotEntity> selectedLotEntities = getLotEntities(penEntity.getPenId(), lotEntities);
            if (selectedLotEntities.size() != 0) {
                for (int p = 0; p < selectedLotEntities.size(); p++) {
                    LotEntity lotEntity = selectedLotEntities.get(p);
                    String lotName = lotEntity.getLotName();
                    String lotId = lotEntity.getLotId();
                    ShuffleObject lotShuffleObject = new ShuffleObject(ShufflePenAndLotsAdapter.LOT_NAME, lotName, lotId);
                    shuffleObjects.add(lotShuffleObject);
                }
            }

        }

        mShuffleAdapter.setAdapterVariables(shuffleObjects, mContext);

    }

    private ArrayList<LotEntity> getLotEntities(String penId, ArrayList<LotEntity> lotEntities) {
        ArrayList<LotEntity> selectedLotEntities = new ArrayList<>();
        for (int o = 0; o < lotEntities.size(); o++) {
            LotEntity lotEntity = lotEntities.get(o);
            if (lotEntity.getPenId().equals(penId)) {
                selectedLotEntities.add(lotEntity);
            }
        }
        return selectedLotEntities;
    }
}
