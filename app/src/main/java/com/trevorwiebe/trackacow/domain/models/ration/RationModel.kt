package com.trevorwiebe.trackacow.domain.models.ration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RationModel(
        var rationPrimaryKey: Int = 0,
        var rationCloudDatabaseId: String = "",
        var rationName: String = ""
): Parcelable
