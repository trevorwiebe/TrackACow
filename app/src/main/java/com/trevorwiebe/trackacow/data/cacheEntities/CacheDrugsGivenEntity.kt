package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "cache_drugs_given")
data class CacheDrugsGivenEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var drugGivenId: String? = "",
    var drugId: String? = "",
    var amountGiven: Int = 0,
    var cowId: String? = "",
    var lotId: String = "",
    var date: Long = 0,
    var whatHappened: Int = 0
)