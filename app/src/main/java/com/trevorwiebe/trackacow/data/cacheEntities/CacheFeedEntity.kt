package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_feed")
class CacheFeedEntity(
    @PrimaryKey
    var id: String = "",
    var feed: Int = 0,
    var date: Long = 0,
    var lotId: String = "",
    var rationCloudId: String? = "",
    var whatHappened: Int = 0
)