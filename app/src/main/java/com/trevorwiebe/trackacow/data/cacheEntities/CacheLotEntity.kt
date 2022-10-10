package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "holdingLot")
class CacheLotEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var lotName: String? = null,
    var lotCloudDatabaseId: String? = null,
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var lotPenCloudDatabaseId: String? = null,
    var whatHappened: Int = 0
)