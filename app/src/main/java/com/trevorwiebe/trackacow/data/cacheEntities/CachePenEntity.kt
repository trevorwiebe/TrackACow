package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "HoldingPen")
data class CachePenEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int,
    var penId: String,
    var penName: String,
    var whatHappened: Int
)