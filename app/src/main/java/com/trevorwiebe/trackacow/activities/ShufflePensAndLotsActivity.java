package com.trevorwiebe.trackacow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.ShufflePenAndLotsAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.dataLoaders.QueryLots;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.objects.ShuffleObject;
import com.trevorwiebe.trackacow.utils.DragHelper;

import java.util.ArrayList;

public class ShufflePensAndLotsActivity extends AppCompatActivity implements
        QueryAllPens.OnPensLoaded,
        QueryLots.OnLotsLoaded {

    private ArrayList<PenEntity> mPenEntities = new ArrayList<>();
    private ShufflePenAndLotsAdapter mShuffleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_pens_and_lots);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RecyclerView shuffleRv = findViewById(R.id.shuffle_rv);
        shuffleRv.setLayoutManager(new LinearLayoutManager(this));

        mShuffleAdapter = new ShufflePenAndLotsAdapter();

        DragHelper dragHelper = new DragHelper(mShuffleAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragHelper);
        mShuffleAdapter.setTouchHelper(itemTouchHelper);

        shuffleRv.setAdapter(mShuffleAdapter);

        itemTouchHelper.attachToRecyclerView(shuffleRv);

        new QueryAllPens(this).execute(this);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        mPenEntities = penEntitiesList;
        new QueryLots(ShufflePensAndLotsActivity.this).execute(ShufflePensAndLotsActivity.this);
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

            LotEntity lotEntity = getLotEntity(penEntity.getPenId(), lotEntities);
            if (lotEntity != null) {
                String lotName = lotEntity.getLotName();
                String lotId = lotEntity.getLotId();
                ShuffleObject lotShuffleObject = new ShuffleObject(ShufflePenAndLotsAdapter.LOT_NAME, lotName, lotId);
                shuffleObjects.add(lotShuffleObject);
            }
        }

        mShuffleAdapter.setAdapterVariables(shuffleObjects, ShufflePensAndLotsActivity.this);

    }

    private LotEntity getLotEntity(String penId, ArrayList<LotEntity> lotEntities) {
        for (int o = 0; o < lotEntities.size(); o++) {
            LotEntity lotEntity = lotEntities.get(o);
            if (lotEntity.getPenId().equals(penId)) {
                return lotEntity;
            }
        }
        return null;
    }
}
