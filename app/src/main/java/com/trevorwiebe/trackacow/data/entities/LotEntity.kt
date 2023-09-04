package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "lot")
class LotEntity (
        @PrimaryKey(autoGenerate = true)
        var lotPrimaryKey: Int = 0,
        var lotName: String = "",
        var lotCloudDatabaseId: String = "",
        var customerName: String? = null,
        var rationId: String? = null,
        var notes: String? = null,
        var date: Long = 0,
        var archived: Long = 0,
        var dateArchived: Long = 0,
        var lotPenCloudDatabaseId: String = ""
)