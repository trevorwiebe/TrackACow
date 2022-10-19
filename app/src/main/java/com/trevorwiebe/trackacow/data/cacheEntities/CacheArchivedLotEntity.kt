package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "holdingArchivedLot")
data class CacheArchivedLotEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = "",
    var notes: String? = "",
    var dateStarted: Long = 0,
    var dateEnded: Long = 0,
    var whatHappened: Int = 0
)