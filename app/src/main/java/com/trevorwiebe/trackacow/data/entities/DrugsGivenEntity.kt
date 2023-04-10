package com.trevorwiebe.trackacow.data.entities

import androidx.room.PrimaryKey
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "DrugsGiven")
data class DrugsGivenEntity (
        @PrimaryKey(autoGenerate = true)
        // TODO add this to database migration local and cloud

        // OLD - primaryKey
        var drugsGivenPrimaryKey: Int = 0,
        var drugsGivenId: String? = "",

        // OLD - drugId
        var drugsGivenDrugId: String? = "",

        // OLD - amountGiven
        var drugsGivenAmountGiven: Int = 0,

        // OLD - cowId
        var drugsGivenCowId: String? = "",

        // OLD - lotId
        var drugsGivenLotId: String? = "",

        // OLD - date
        var drugsGivenDate: Long = 0
)