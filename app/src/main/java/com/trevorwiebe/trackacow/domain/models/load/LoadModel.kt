package com.trevorwiebe.trackacow.domain.models.load

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoadModel(
    var primaryKey: Int = 0,
    var numberOfHead: Int = 0,
    var date: Long = 0,
    var description: String? = "",
    var lotId: String? = "",
    var loadId: String? = ""
): Parcelable
