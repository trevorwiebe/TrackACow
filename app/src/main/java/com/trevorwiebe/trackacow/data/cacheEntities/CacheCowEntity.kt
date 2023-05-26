package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "cache_cow")
data class CacheCowEntity (
        @PrimaryKey(autoGenerate = true)
        var primaryKey: Int = 0,
        var alive: Int = 0,
        var cowId: String = "",
        var tagNumber: Int = 0,
        var date: Long = 0,
        var notes: String? = "",
        var lotId: String = "",
        var whatHappened: Int = 0
)