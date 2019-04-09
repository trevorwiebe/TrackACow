package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "feed")
public class FeedEntity {

    public static final String FEED = "feed";

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

    public FeedEntity(int feed, long date, String id, String lotId) {
        this.feed = feed;
        this.date = date;
        this.id = id;
        this.lotId = lotId;
    }

    @Ignore
    public FeedEntity() {
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
}
