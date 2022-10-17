package com.trevorwiebe.trackacow.domain.models.drug

import android.os.Parcelable
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DrugModel (
    var primaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugCloudDatabaseId: String = "",
    var drugName: String = ""
): Parcelable