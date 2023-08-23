package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "feed")
class FeedEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var feed: Int = 0,
    var date: Long = 0,
    var id: String = "",
    var lotId: String = "",
    var rationCloudId: String? = ""
)