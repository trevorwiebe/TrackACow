package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "lot")
class LotEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var lotName: String = "",
    var lotId: String = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var lotPenId: String = ""
)