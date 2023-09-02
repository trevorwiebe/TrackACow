package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "feed")
class FeedEntity(
    @PrimaryKey
    var id: String = "",
    var feed: Int = 0,
    var date: Long = 0,
    var lotId: String = "",
    var rationCloudId: String? = ""
)