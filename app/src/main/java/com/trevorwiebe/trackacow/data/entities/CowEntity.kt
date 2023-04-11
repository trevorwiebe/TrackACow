package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "cow")
data class CowEntity (
    @PrimaryKey(autoGenerate = true)
    // nothing changes in new version
    var primaryKey: Int = 0,
    var isAlive: Int = 0,
    var cowId: String = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String = ""
)