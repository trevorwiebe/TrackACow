package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "drug")
data class DrugEntity(
        @PrimaryKey(autoGenerate = true)
        var drugPrimaryKey: Int = 0,
        var defaultAmount: Int = 0,
        var drugCloudDatabaseId: String = "",
        var drugName: String = ""
)