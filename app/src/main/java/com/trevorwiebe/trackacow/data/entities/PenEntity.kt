package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "Pen")
data class PenEntity (
    @PrimaryKey(autoGenerate = true)
    var penPrimaryKey: Int = 0,
    var penCloudDatabaseId: String? = null,
    var penName: String = "",
)