package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "Drug")
data class DrugEntity(
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration local and cloud

        // OLD - primaryKey
        var drugPrimaryKey: Int = 0,
        var defaultAmount: Int = 0,
        // OLD - drugId
        var drugCloudDatabaseId: String = "",
        var drugName: String = ""
)