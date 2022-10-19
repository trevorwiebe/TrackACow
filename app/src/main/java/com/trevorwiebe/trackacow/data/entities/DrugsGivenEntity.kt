package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "DrugsGiven")
data class DrugsGivenEntity (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var drugGivenId: String? = "",
    var drugId: String? = "",
    var amountGiven: Int = 0,
    var cowId: String? = "",
    var lotId: String? = "",
    var date: Long = 0
)