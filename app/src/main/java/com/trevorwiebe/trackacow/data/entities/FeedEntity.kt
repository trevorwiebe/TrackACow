package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "feed")
class FeedEntity (
    @PrimaryKey(autoGenerate = true)
    // nothing changes in new version
    var primaryKey: Int = 0,
    var feed: Int = 0,
    var date: Long = 0,
    var id: String = "",
    var lotId: String = ""
)