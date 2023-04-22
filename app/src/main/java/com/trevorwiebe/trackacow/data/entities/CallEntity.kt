package com.trevorwiebe.trackacow.data.entities

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import androidx.room.Entity

@Keep
@Entity(tableName = "call")
data class CallEntity (
        @PrimaryKey(autoGenerate = true)
        var callPrimaryKey: Int = 0,
        var callAmount: Int = 0,
        var date: Long = 0,
        var lotId: String = "",
        val callRationId: Int? = 0,
        var callCloudDatabaseId: String? = ""
)