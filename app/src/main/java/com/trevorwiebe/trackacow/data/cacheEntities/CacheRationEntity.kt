package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "holding_ration")
data class CacheRationEntity(
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var rationId: String,
    var rationName: String,
    var whatHappened: Int
)
