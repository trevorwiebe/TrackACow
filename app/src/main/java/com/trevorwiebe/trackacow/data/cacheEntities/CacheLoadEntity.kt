package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "holdingLoad")
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