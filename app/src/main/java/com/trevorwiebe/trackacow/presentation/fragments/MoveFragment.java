package com.trevorwiebe.trackacow.presentation.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.adapters.ShufflePenAndLotsAdapter;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.pen.QueryAllPens;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLots;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.domain.objects.ShuffleObject;
import com.trevorwiebe.trackacow.domain.utils.DragHelper;

import java.util.ArrayList;

public class MoveFragment extends Fragment implements
        QueryAllPens.OnPensLoaded,
        QueryLots.OnLotsLoaded {

    private RecyclerView mMoveRv;
    private TextView mEmptyMoveList;

    private ArrayList<PenEntity> mPenEntities = new ArrayList<>();
    private ShufflePenAndLotsAdapter mShuffleAdapter;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_move, container, false);

        mMoveRv = rootView.findViewById(R.id.shuffle_rv);
        mEmptyMoveList = rootView.findViewById(R.id.empty_move_tv);

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
            String penId = penEntity.getPenCloudDatabaseId();

            ShuffleObject penShuffleObject = new ShuffleObject(ShufflePenAndLotsAdapter.PEN_NAME, penName, penId);
            shuffleObjects.add(penShuffleObject);

            ArrayList<LotEntity> selectedLotEntities = getLotEntities(penEntity.getPenCloudDatabaseId(), lotEntities);
            if (selectedLotEntities.size() != 0) {
                for (int p = 0; p < selectedLotEntities.size(); p++) {
                    LotEntity lotEntity = selectedLotEntities.get(p);
                    String lotName = lotEntity.getLotName();
                    String lotId = lotEntity.getLotCloudDatabaseId();
                    ShuffleObject lotShuffleObject = new ShuffleObject(ShufflePenAndLotsAdapter.LOT_NAME, lotName, lotId);
                    shuffleObjects.add(lotShuffleObject);
                }
            }

        }

        if (shuffleObjects.size() == 0) {
            mEmptyMoveList.setVisibility(View.VISIBLE);
        } else {
            mEmptyMoveList.setVisibility(View.INVISIBLE);
        }
        mShuffleAdapter.setAdapterVariables(shuffleObjects, mContext);

    }

    private ArrayList<LotEntity> getLotEntities(String penId, ArrayList<LotEntity> lotEntities) {
        ArrayList<LotEntity> selectedLotEntities = new ArrayList<>();
        for (int o = 0; o < lotEntities.size(); o++) {
            LotEntity lotEntity = lotEntities.get(o);
            if (lotEntity.getLotPenCloudDatabaseId().equals(penId)) {
                selectedLotEntities.add(lotEntity);
            }
        }
        return selectedLotEntities;
    }
}
