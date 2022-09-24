package com.trevorwiebe.trackacow.data.local.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.local.entities.FeedEntity;

import java.util.List;

@Dao
public interface FeedDao {

    @Insert(onConflict = REPLACE)
    void insertFeedEntity(FeedEntity feedEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFeedEntityList(List<FeedEntity> feedEntities);

    @Query("SELECT * FROM feed")
    List<FeedEntity> getAllFeedEntities();

    @Query("SELECT * FROM feed WHERE lotId = :lotId")
    List<FeedEntity> getFeedEntitiesByLotId(String lotId);

    @Query("SELECT * FROM feed WHERE lotId = :lotId AND date = :date")
    List<FeedEntity> getFeedEntitiesByLotAndDate(String lotId, long date);

    @Query("DELETE FROM feed WHERE date = :date AND lotId = :lotId")
    int deleteFeedEntitiesByDateAndLotId(long date, String lotId);

    @Query("DELETE FROM feed WHERE id = :id")
    int deleteFeedEntityById(String id);

    @Query("DELETE FROM feed")
    void deleteFeedTable();

    @Update
    void updateFeedEntity(FeedEntity feedEntity);

    @Delete
    void deleteFeedEntity(FeedEntity feedEntity);

}
