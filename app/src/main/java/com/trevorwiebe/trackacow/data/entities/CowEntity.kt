package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Keep
@Entity(tableName = "Cow")
data class CowEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var isAlive: Int = 0,
    var cowId: String? = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String? = ""
)