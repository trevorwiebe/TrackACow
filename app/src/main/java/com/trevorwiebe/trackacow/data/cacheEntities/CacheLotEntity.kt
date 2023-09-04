package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_lot")
class CacheLotEntity (
    @PrimaryKey(autoGenerate = true)
    var lotPrimaryKey: Int = 0,
    var lotName: String = "",
    var lotCloudDatabaseId: String = "",
    var customerName: String? = "",
    var rationId: String? = null,
    var notes: String? = "",
    var date: Long = 0,
    var archived: Long = 0,
    var dateArchived: Long = 0,
    var lotPenCloudDatabaseId: String = "",
    var whatHappened: Int = 0
)