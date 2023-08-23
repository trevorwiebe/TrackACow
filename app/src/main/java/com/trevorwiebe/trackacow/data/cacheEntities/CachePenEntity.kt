package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_pen")
data class CachePenEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int,
    var penCloudDatabaseId: String?,
    var penName: String,
    var whatHappened: Int
)