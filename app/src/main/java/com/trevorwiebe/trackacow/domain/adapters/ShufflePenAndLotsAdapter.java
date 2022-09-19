package com.trevorwiebe.trackacow.domain.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot.UpdateHoldingLot;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.UpdateLotWithNewPenId;
import com.trevorwiebe.trackacow.data.db.entities.LotEntity;
import com.trevorwiebe.trackacow.domain.objects.ShuffleObject;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.DragHelper;
import com.trevorwiebe.trackacow.domain.utils.LotViewHolder;
import com.trevorwiebe.trackacow.domain.utils.PenViewHolder;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;


public class ShufflePenAndLotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        DragHelper.ActionCompletionContract {

    private static final String TAG = "ShufflePenAndLotsAdapte";

    public static final int LOT_NAME = 1;
    public static final int PEN_NAME = 2;
    private ArrayList<ShuffleObject> shuffleObjects = new ArrayList<>();
    private Context context;
    private ItemTouchHelper touchHelper;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case LOT_NAME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shuffle_lot, parent, false);
                return new LotViewHolder(view);
            case PEN_NAME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shuffle_pen, parent, false);
                return new PenViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shuffle_lot, parent, false);
                return new LotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);

        ShuffleObject shuffleObject = shuffleObjects.get(position);

        if (itemViewType == LOT_NAME) {
            String lotName = shuffleObject.getName();
            final LotViewHolder lotViewHolder = (LotViewHolder) holder;
            lotViewHolder.lotName.setText(lotName);
            lotViewHolder.reorder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                        lotViewHolder.lotMoveView.setVisibility(View.VISIBLE);
                        return true;
                    } else {
                        lotViewHolder.lotMoveView.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
            });
        } else {
            String penName = shuffleObject.getName();
            String penText = "Pen: " + penName;
            PenViewHolder penViewHolder = (PenViewHolder) holder;
            penViewHolder.penName.setText(penText);
        }
    }

    @Override
    public int getItemCount() {
        return shuffleObjects == null ? 0 : shuffleObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        ShuffleObject shuffleObject = shuffleObjects.get(position);
        return shuffleObject.getType();
    }

    public void setAdapterVariables(ArrayList<ShuffleObject> shuffleObjects, Context context) {
        this.shuffleObjects = new ArrayList<>(shuffleObjects);
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

        if (newPosition != 0) {

            ShuffleObject lotShuffleObject = shuffleObjects.get(oldPosition);
            String lotId = lotShuffleObject.getId();

            shuffleObjects.remove(oldPosition);
            shuffleObjects.add(newPosition, lotShuffleObject);
            notifyItemMoved(oldPosition, newPosition);

            ShuffleObject penShuffleObject = findNearestPen(newPosition);
            if (penShuffleObject != null) {
                String penId = penShuffleObject.getId();

                if (Utility.haveNetworkConnection(context)) {
                    DatabaseReference baseRef = Constants.BASE_REFERENCE.child(LotEntity.LOT).child(lotId).child("penId");
                    baseRef.setValue(penId);
                } else {
                    Utility.setNewDataToUpload(context, true);
                    new UpdateHoldingLot(lotId, penId).execute(context);
                }
                new UpdateLotWithNewPenId(lotId, penId).execute(context);
            }

        }
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    private ShuffleObject findNearestPen(int position) {
        for (int i = position; i < shuffleObjects.size(); i--) {
            ShuffleObject shuffleObject = shuffleObjects.get(i);
            if (shuffleObject.getType() == PEN_NAME) {
                return shuffleObject;
            }
        }
        return null;
    }
}
