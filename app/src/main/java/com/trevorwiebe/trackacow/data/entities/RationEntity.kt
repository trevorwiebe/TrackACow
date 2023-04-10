package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "ration")
data class RationEntity(
    @PrimaryKey(autoGenerate = true)
    // TODO not included, need ta add new table
    var rationPrimaryKey: Int = 0,
    var rationCloudDatabaseId: String? = "",
    var rationName: String = ""
)
