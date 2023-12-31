package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_ration")
data class CacheRationEntity(
        @PrimaryKey(autoGenerate = true)
        var rationPrimaryKey: Int = 0,
        var rationCloudDatabaseId: String,
        var rationName: String,
        var whatHappened: Int
)
