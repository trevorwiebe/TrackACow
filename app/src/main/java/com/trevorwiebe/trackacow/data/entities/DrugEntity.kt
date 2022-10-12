package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "Drug")
data class DrugEntity(
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugId: String = "",
    var drugName: String = ""
)