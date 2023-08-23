package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "pen")
data class PenEntity (
        @PrimaryKey(autoGenerate = true)
        var penPrimaryKey: Int = 0,
        var penCloudDatabaseId: String? = null,
        var penName: String = "",
)