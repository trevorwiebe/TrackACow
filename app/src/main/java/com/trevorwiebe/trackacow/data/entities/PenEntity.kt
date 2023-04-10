package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "Pen")
data class PenEntity (
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration local and cloud

        // OLD - primaryKey
        var penPrimaryKey: Int = 0,

        // OLD - penId
        var penCloudDatabaseId: String? = null,
        var penName: String = "",
)