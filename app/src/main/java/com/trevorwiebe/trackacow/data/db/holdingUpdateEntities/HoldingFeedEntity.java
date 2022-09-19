package com.trevorwiebe.trackacow.data.db.holdingUpdateEntities;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.trevorwiebe.trackacow.data.db.entities.FeedEntity;

@Keep
@Entity(tableName = "holdingFeed")
public class HoldingFeedEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "feed")
    private int feed;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingFeedEntity(int primaryKey, int feed, long date, String id, String lotId, int whatHappened) {
        this.primaryKey = primaryKey;
        this.feed = feed;
        this.date = date;
        this.id = id;
        this.lotId = lotId;
        this.whatHappened = whatHappened;
    }

    public HoldingFeedEntity(FeedEntity feedEntity, int whatHappened){
        this.primaryKey = feedEntity.getPrimaryKey();
        this.feed = feedEntity.getFeed();
        this.date = feedEntity.getDate();
        this.id = feedEntity.getId();
        this.lotId = feedEntity.getLotId();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingFeedEntity() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getFeed() {
        return feed;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
