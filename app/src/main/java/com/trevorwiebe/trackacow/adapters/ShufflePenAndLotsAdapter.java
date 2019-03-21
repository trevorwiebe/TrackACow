package com.trevorwiebe.trackacow.adapters;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.UpdateHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.UpdateLotWithNewPenId;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.objects.ShuffleObject;
import com.trevorwiebe.trackacow.utils.DragHelper;
import com.trevorwiebe.trackacow.utils.LotViewHolder;
import com.trevorwiebe.trackacow.utils.PenViewHolder;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.ListIterator;


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
            LotViewHolder lotViewHolder = (LotViewHolder) holder;
            lotViewHolder.lotName.setText(lotName);
            lotViewHolder.reorder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                    }
                    return false;
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
                    DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(LotEntity.LOT).child(lotId).child("penId");
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
