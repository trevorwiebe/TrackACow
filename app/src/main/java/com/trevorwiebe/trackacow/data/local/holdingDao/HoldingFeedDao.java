package com.trevorwiebe.trackacow.data.local.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingFeedEntity;

import java.util.List;

@Dao
public interface HoldingFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHoldingFeed(HoldingFeedEntity holdingFeedEntity);

    @Insert
    void insertHoldingFeedList(List<HoldingFeedEntity> holdingFeedEntities);

    @Query("SELECT * FROM holdingFeed")
    List<HoldingFeedEntity> getHoldingFeedEntities();

    @Query("DELETE FROM holdingFeed")
    void deleteHoldingFeedTable();

    @Update
    void updateHoldingFeedEntity(HoldingFeedEntity holdingFeedEntity);

    @Delete
    void deleteFeedEntity(HoldingFeedEntity holdingFeedEntity);
}
