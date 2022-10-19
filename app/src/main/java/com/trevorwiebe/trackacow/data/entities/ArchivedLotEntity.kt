package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "archivedLot")
data class ArchivedLotEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = "",
    var notes: String? = "",
    var dateStarted: Long = 0,
    var dateEnded: Long = 0
)