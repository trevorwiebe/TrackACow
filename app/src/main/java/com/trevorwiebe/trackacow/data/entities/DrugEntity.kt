package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "drug")
data class DrugEntity(
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration cloud
        var drugPrimaryKey: Int = 0,
        var defaultAmount: Int = 0,
        var drugCloudDatabaseId: String = "",
        var drugName: String = ""
)