package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_load")
data class CacheLoadEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var numberOfHead: Int = 0,
    var date: Long = 0,
    var description: String? = "",
    var lotId: String? = "",
    var loadId: String? = "",
    var whatHappened: Int = 0
)