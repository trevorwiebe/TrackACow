package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_call")
data class CacheCallEntity (
    @PrimaryKey(autoGenerate = true)
    var callPrimaryKey: Int = 0,
    var callAmount: Int,
    var date: Long,
    var lotId: String,
    var callRationId: Int? = null,
    var callCloudDatabaseId: String?,
    var whatHappened: Int
)