package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "drugs_given")
data class DrugsGivenEntity (
        @PrimaryKey(autoGenerate = true)
        var drugsGivenPrimaryKey: Int = 0,
        var drugsGivenId: String? = "",
        var drugsGivenDrugId: String? = "",
        var drugsGivenAmountGiven: Int = 0,
        var drugsGivenCowId: String? = "",
        var drugsGivenLotId: String? = "",
        var drugsGivenDate: Long = 0
)