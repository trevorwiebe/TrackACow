package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "pen")
data class PenEntity (
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration cloud
        var penPrimaryKey: Int = 0,
        var penCloudDatabaseId: String? = null,
        var penName: String = "",
)