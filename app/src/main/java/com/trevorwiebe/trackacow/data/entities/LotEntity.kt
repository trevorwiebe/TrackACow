package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "lot")
class LotEntity (
    @PrimaryKey(autoGenerate = true)
    var lotPrimaryKey: Int = 0,
    var lotName: String = "",
    var lotCloudDatabaseId: String = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var lotPenCloudDatabaseId: String = ""
)