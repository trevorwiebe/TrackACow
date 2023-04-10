package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "lot")
class LotEntity (
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration local and cloud

        // OLD - primaryKey
        var lotPrimaryKey: Int = 0,
        var lotName: String = "",

        // OLD - lotId
        var lotCloudDatabaseId: String = "",
        var customerName: String? = null,
        var notes: String? = null,
        var date: Long = 0,

        // OLD - ~not included, need to add~
        var archived: Long = 0,

        // OLD - ~not included, need to add~
        var dateArchived: Long? = 0,

        // OLD - penId
        var lotPenCloudDatabaseId: String = ""
)