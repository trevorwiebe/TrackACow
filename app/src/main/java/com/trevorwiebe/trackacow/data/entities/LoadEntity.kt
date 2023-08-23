package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "load")
data class LoadEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var numberOfHead: Int = 0,
    var date: Long = 0,
    var description: String? = "",
    var lotId: String? = "",
    var loadId: String? = ""
)