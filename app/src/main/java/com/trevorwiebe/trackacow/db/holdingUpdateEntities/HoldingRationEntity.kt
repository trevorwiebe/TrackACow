package com.trevorwiebe.trackacow.db.holdingUpdateEntities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "holding_ration")
data class HoldingRationEntity(
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var rationId: String,
    var rationName: String,
    var whatHappened: Int
)
