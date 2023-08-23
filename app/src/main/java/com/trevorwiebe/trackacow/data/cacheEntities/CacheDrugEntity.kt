package com.trevorwiebe.trackacow.data.cacheEntities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "cache_drug")
class CacheDrugEntity (
    @PrimaryKey(autoGenerate = true)
    var drugPrimaryKey: Int,
    var defaultAmount: Int,
    var drugCloudDatabaseId: String,
    var drugName: String,
    var whatHappened: Int
)