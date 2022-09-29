package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity;

import java.util.List;

@Dao
public interface CacheFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHoldingFeed(CacheFeedEntity cacheFeedEntity);

    @Insert
    void insertHoldingFeedList(List<CacheFeedEntity> holdingFeedEntities);

    @Query("SELECT * FROM holdingFeed")
    List<CacheFeedEntity> getHoldingFeedEntities();

    @Query("DELETE FROM holdingFeed")
    void deleteHoldingFeedTable();

    @Update
    void updateHoldingFeedEntity(CacheFeedEntity cacheFeedEntity);

    @Delete
    void deleteFeedEntity(CacheFeedEntity cacheFeedEntity);
}
