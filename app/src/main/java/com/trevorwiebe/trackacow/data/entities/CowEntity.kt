package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "cow")
data class CowEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var alive: Int = 0,
    var cowId: String = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String = ""
)