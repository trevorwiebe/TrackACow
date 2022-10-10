package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "call")
data class CallEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var callAmount: Int,
    var date: Long,
    var lotId: String,
    var callCloudDatabaseId: String?
)