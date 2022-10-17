package com.trevorwiebe.trackacow.domain.models.ration

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RationModel(
    var rationPrimaryKey: Int,
    var rationCloudDatabaseId: String?,
    var rationName: String
): Parcelable
