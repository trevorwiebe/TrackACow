package com.trevorwiebe.trackacow.domain.models.drug

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrugModel (
    var drugPrimaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugCloudDatabaseId: String = "",
    var drugName: String = ""
): Parcelable