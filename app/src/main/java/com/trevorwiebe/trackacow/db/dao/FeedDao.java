package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.FeedEntity;

import java.util.List;

@Dao
public interface FeedDao {

    @Insert
    void insertFeedEntity(FeedEntity feedEntity);

    @Insert
    void insertFeedEntityList(List<FeedEntity> feedEntities);

    @Query("SELECT * FROM feed")
    List<FeedEntity> getAllFeedEntities();

    @Query("SELECT * FROM feed WHERE lotId = :lotId")
    List<FeedEntity> getFeedEntitiesByLotId(String lotId);

    @Query("SELECT * FROM feed WHERE lotId = :lotId AND date = :date")
    List<FeedEntity> getFeedEntitiesByLotAndDate(String lotId, long date);

    @Query("DELETE FROM feed WHERE date = :date AND lotId = :lotId")
    int deleteFeedEntitiesByDateAndLotId(long date, String lotId);

    @Query("DELETE FROM feed")
    void deleteFeedTable();

    @Update
    void updateFeedEntity(FeedEntity feedEntity);

    @Delete
    void deleteFeedEntity(FeedEntity feedEntity);

}
