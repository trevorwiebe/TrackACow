package com.trevorwiebe.trackacow.data.db.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "ration")
data class RationEntity(
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var rationId: String,
    var rationName: String
)
