package com.trevorwiebe.trackacow.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ration")
data class RationEntity(
        @PrimaryKey(autoGenerate = true)
        var rationPrimaryKey: Int = 0,
        var rationCloudDatabaseId: String = "",
        var rationName: String = ""
)
